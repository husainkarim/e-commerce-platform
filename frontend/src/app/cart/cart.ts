import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ApiService } from '../api.service';
import { AuthServiceService } from '../auth-service.service';
import { forkJoin } from 'rxjs';

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

interface userCart {
  userId: string;
  items: CartItem[];
}

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './cart.html',
  styleUrl: './cart.css',
})
export class Cart {
  items: Product[] = [];
  cartItems: CartItem[] = [];
  userCartData: userCart | null = null;

  constructor(private apiService: ApiService, private authService: AuthServiceService, private router: Router) {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }
    this.loadCart();
  }

  get subtotal(): number {
    return this.items.reduce((sum, item) => sum + item.price * item.quantity, 0);
  }

  get total(): number {
    return this.subtotal;
  }

  increment(item: Product): void {
    this.updateQuantity(item, item.quantity + 1);
  }

  decrement(item: Product): void {
    if (item.quantity > 1) {
      this.updateQuantity(item, item.quantity - 1);
    }
  }

  updateQuantity(item: Product, value: number | string): void {
    const parsed = Number(value);
    const nextQuantity = Number.isFinite(parsed) ? Math.min(Math.max(Math.trunc(parsed), 1), 99) : 1;
    item.quantity = nextQuantity;
    this.persistCart();
  }

  removeItem(id: string): void {
    this.items = this.items.filter((item) => item.id !== id);
    this.persistCart();
  }

  private loadCart(): void {
    this.apiService.getCartByUserId(this.authService.getUser().id).subscribe({
      next: (response) => {
        this.cartItems = response.cart.items || [];
        localStorage.setItem('cartItems', JSON.stringify(this.cartItems));
        this.items = [];

        for (const cartItem of this.cartItems) {
          forkJoin({
            productRes: this.apiService.getProductById(cartItem.productId),
            imageRes: this.apiService.getImagesByProductId(cartItem.productId)
          }).subscribe({
            next: ({ productRes, imageRes }) => {

              const image =
                imageRes?.images?.length > 0
                  ? imageRes.images[0].imagePath
                  : 'assets/product-images/default-product-image.jpg';

              const item: Product = {
                id: productRes.product.id,
                name: productRes.product.name,
                description: productRes.product.description,
                price: productRes.product.price,
                quantity: cartItem.quantity,
                userId: productRes.product.userId,
                category: productRes.product.category,
                image: image
              };

              this.items.push(item);
              console.log('Loaded cart item:', item);
            },
            error: (err) => {
              console.error('Failed to load product or image:', err);
            }
          });
        }
      },
      error: (error) => {
        console.error('Failed to fetch cart data:', error);
      }
    });
  }

  private persistCart(): void {
    console.log('Persisting cart:', this.items);
    this.userCartData = {
      userId: this.authService.getUser().id,
      items: this.items.map((item) => ({
        sellerId: item.userId,
        productId: item.id,
        productName: item.name,
        price: item.price,
        quantity: item.quantity,
      })),
    };
    this.apiService.updateCart(this.userCartData.userId, this.userCartData).subscribe({
      next: (response) => console.log('Cart updated successfully on server:', response),
      error: (error) => console.error('Failed to update cart on server:', error)
    });
    localStorage.setItem('cartItems', JSON.stringify(this.userCartData.items));
  }
}
