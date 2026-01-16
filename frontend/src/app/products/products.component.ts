import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../api.service';
import { AuthServiceService } from '../auth-service.service';

interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  quantity: number;
  userId: string;
  image: string;
  category?: string;
}

interface CartItem {
  sellerId: string;
  productId: string;
  productName: string;
  price: number;
  quantity: number;
}

interface Cart {
  userId: string;
  items: CartItem[];
}

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css']
})
export class ProductsComponent {
  products: Product[] = [];
  filteredProducts: Product[] = [];
  paginatedProducts: Product[] = [];

  // Search & Filters
  searchKeyword = '';
  selectedCategory = '';
  minPrice: number | null = null;
  maxPrice: number | null = null;
  sortOption = '';

  // Pagination
  currentPage = 1;
  itemsPerPage = 12;
  totalPages = 1;

  categories: string[] = ['Automotive', 'Beauty', 'Books', 'Electronics', 'Fashion', 'Garden', 'General', 'Groceries', 'Health', 'Home', 'Jewelry', 'Movies', 'Music', 'Sports', 'Toys'];

  constructor(private apiService: ApiService, private authService: AuthServiceService) {}

  ngOnInit() {
    this.fetchProducts();
  }

  get startIndex(): number {
    return (this.currentPage - 1) * this.itemsPerPage;
  }

  get endIndex(): number {
    return Math.min(this.startIndex + this.itemsPerPage, this.filteredProducts.length);
  }

  private fetchProducts() {
    this.apiService.getProducts().subscribe({
      next: (response) => {
        console.log('Products fetched successfully:', response);
        this.products = response.products || [];
        for (let product of this.products) {
          this.apiService.getImagesByProductId(product.id).subscribe({
            next: (imageResponse) => {
              if (imageResponse.images && imageResponse.images.length > 0) {
                product.image = imageResponse.images[0].imagePath;
              } else {
                product.image = 'assets/product-images/default-product-image.jpg';
              }
            },
            error: () => {
              product.image = 'assets/product-images/default-product-image.jpg';
            }
          });
        }
        this.applyFilters();
      },
      error: (error) => {
        console.error('Failed to fetch products:', error);
        this.applyFilters();
      }
    });
  }

  applyFilters(): void {
    let results = [...this.products];

    // Keyword search
    if (this.searchKeyword.trim()) {
      const keyword = this.searchKeyword.toLowerCase();
      results = results.filter(
        (p) =>
          p.name.toLowerCase().includes(keyword) ||
          p.description.toLowerCase().includes(keyword)
      );
    }

    // Category filter
    if (this.selectedCategory) {
      results = results.filter((p) => p.category === this.selectedCategory);
    }

    // Price range filter
    if (this.minPrice !== null && this.minPrice >= 0) {
      results = results.filter((p) => p.price >= this.minPrice!);
    }
    if (this.maxPrice !== null && this.maxPrice >= 0) {
      results = results.filter((p) => p.price <= this.maxPrice!);
    }

    // Sort
    switch (this.sortOption) {
      case 'name-asc':
        results.sort((a, b) => a.name.localeCompare(b.name));
        break;
      case 'name-desc':
        results.sort((a, b) => b.name.localeCompare(a.name));
        break;
      case 'price-asc':
        results.sort((a, b) => a.price - b.price);
        break;
      case 'price-desc':
        results.sort((a, b) => b.price - a.price);
        break;
    }

    this.filteredProducts = results;
    this.totalPages = Math.ceil(this.filteredProducts.length / this.itemsPerPage);
    this.currentPage = 1;
    this.updatePagination();
  }

  clearFilters(): void {
    this.searchKeyword = '';
    this.selectedCategory = '';
    this.minPrice = null;
    this.maxPrice = null;
    this.sortOption = '';
    this.applyFilters();
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.updatePagination();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxVisible = 5;
    let start = Math.max(1, this.currentPage - Math.floor(maxVisible / 2));
    let end = Math.min(this.totalPages, start + maxVisible - 1);

    if (end - start < maxVisible - 1) {
      start = Math.max(1, end - maxVisible + 1);
    }

    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
    return pages;
  }

  private updatePagination(): void {
    const start = this.startIndex;
    const end = this.endIndex;
    this.paginatedProducts = this.filteredProducts.slice(start, end);
  }

  addToCart(product: Product): void {
    if (product.quantity === 0) {
      return;
    }
    let cart = localStorage.getItem('cartItems');
    let cartItems = cart ? JSON.parse(cart) : [];

    const existingItem = cartItems.find((item: any) => item.id === product.id);
    if (existingItem) {
      existingItem.quantity += 1;
    } else {
      cartItems.push({
        sellerId: product.userId,
        productId: product.id,
        productName: product.name,
        price: product.price,
        quantity: 1
      });
    }
    let userCart: Cart = {
      userId: this.authService.getUser().id,
      items: cartItems
    }
    this.apiService.updateCart(this.authService.getUser().id, userCart).subscribe({
      next: (response) => {
        console.log('Cart updated successfully:', response);
      },
      error: (error) => {
        console.error('Failed to update cart:', error);
      }
    });
    localStorage.setItem('cartItems', JSON.stringify(userCart.items));
    alert(`${product.name} added to cart!`);
  }
}
