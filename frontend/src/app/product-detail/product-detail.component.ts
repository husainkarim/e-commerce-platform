import { Component } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthServiceService } from '../auth-service.service';
import { ApiService } from '../api.service';

interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  revenue: number;
  quantity: number;
  soldQuantity: number;
  userId: string;
  image: string;
  category?: string;
}

interface CartItem {
  sellerId: string;
  productId: string;
  productName: string;
  category: string;
  price: number;
  quantity: number;
}

interface Cart {
  userId: string;
  items: CartItem[];
}

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.css'
})
export class ProductDetailComponent {
  product: any;
  selectedQuantity: number = 1;

  constructor(private route: ActivatedRoute, private apiService: ApiService, private authServiceService: AuthServiceService, private router: Router) {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.apiService.getProductById(id).subscribe({
        next: (response) => {
          this.product = response.product;
        },
        error: (error) => {
          console.error('Failed to fetch product details:', error);
        }
      });
      this.apiService.getImagesByProductId(id).subscribe({
        next: (response) => {
          if (this.product) {
            let images: string[] = [];
            response.images.forEach((image: { imagePath: string }) => {
              images.push(image.imagePath);
            });
            if (images.length > 0) {
              this.product.images = images;
              this.product.image = images[0];
            } else {
              this.product.image = 'assets/product-images/default-product-image.jpg';
            }
          }
        },
        error: (error) => {
          console.error('Failed to fetch product images:', error);
        }
      });
    } else {
      this.product = null;
      console.warn('No product ID found in route parameters.');
      this.router.navigate(['/not-found']);
    }
  }

  selectImage(img: string) {
    if (this.product) {
      this.product.image = img;
    }
  }

  calculateDiscount(): number {
    if (!this.product?.originalPrice || !this.product?.price) {
      return 0;
    }
    const discount = ((this.product.originalPrice - this.product.price) / this.product.originalPrice) * 100;
    return Math.round(discount);
  }

  increaseQuantity(): void {
    if (this.selectedQuantity < this.product.quantity) {
      this.selectedQuantity++;
    }
  }

  decreaseQuantity(): void {
    if (this.selectedQuantity > 1) {
      this.selectedQuantity--;
    }
  }

  updateQuantity(value: string): void {
    const qty = parseInt(value, 10);
    if (!isNaN(qty) && qty >= 1 && qty <= this.product.quantity) {
      this.selectedQuantity = qty;
    } else if (qty > this.product.quantity) {
      this.selectedQuantity = this.product.quantity;
    } else {
      this.selectedQuantity = 1;
    }
  }

  getTotalPrice(): number {
    return this.product?.price * this.selectedQuantity || 0;
  }

  addToCart(qty: number): void {
    if (this.product.quantity === 0) {
      return;
    }
    let cart = localStorage.getItem('cartItems');
    let cartItems = cart ? JSON.parse(cart) : [];

    const existingItem = cartItems.find((item: any) => item.productId === this.product.id);
    if (existingItem) {
      existingItem.quantity += qty;
    } else {
      cartItems.push({
        sellerId: this.product.userId,
        productId: this.product.id,
        productName: this.product.name,
        category: this.product.category,
        price: this.product.price,
        quantity: qty
      });
    }
    let userCart: Cart = {
      userId: this.authServiceService.getUser().id,
      items: cartItems
    }
    console.log('product added to cart:', this.product);
    console.log('Updating cart for user:', userCart);
    this.apiService.updateCart(this.authServiceService.getUser().id, userCart).subscribe({
      next: (response) => {
        console.log('Cart updated successfully:', response);
      },
      error: (error) => {
        console.error('Failed to update cart:', error);
      }
    });
    localStorage.setItem('cartItems', JSON.stringify(userCart.items));
    alert(`${qty} x ${this.product.name} added to cart!`);
    this.selectedQuantity = 1; // Reset quantity after adding to cart
  }

  saveForLater() {
    console.log('Saving product for later:', this.product);
    // In real app: this.apiService.saveForLater(this.product.id);
  }
}
