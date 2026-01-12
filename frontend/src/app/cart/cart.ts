import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
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
  items: any[] = [];

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

  increment(item: CartItem): void {
    this.updateQuantity(item, item.quantity + 1);
  }

  decrement(item: CartItem): void {
    if (item.quantity > 1) {
      this.updateQuantity(item, item.quantity - 1);
    }
  }

  updateQuantity(item: CartItem, value: number | string): void {
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
    const stored = localStorage.getItem('cartItems');
    if (!stored) {
      try {
        this.apiService.GetCartByUserId(this.authService.getUser().id).subscribe({
          next: (response) => {
            let cartItems: CartItem[] = response.items || [];
            this.items = cartItems.map((cartItem) =>
              this.apiService.getProductById(cartItem.productId).subscribe({
                next: (productResponse) => {
                  return {
                    id: productResponse.id,
                    name: productResponse.name,
                    description: productResponse.description,
                    price: productResponse.price,
                    quantity: cartItem.quantity,
                    userId: productResponse.userId,
                    image: productResponse.image,
                    category: productResponse.category,
                  } as Product;
                },
                error: (error: any) => {
                  console.error('Failed to fetch product data:', error);
                  return null;
                }
              })
            ),
            this.persistCart();
          },
          error: (error) => {
            console.error('Failed to fetch cart data:', error);
          }
        });
        return;
      } catch (error) {
        console.warn('Falling back to sample cart items because stored data is invalid.', error);
      }
    }
    this.persistCart();
  }

  private persistCart(): void {
    this.apiService.UpdateCart(this.authService.getUser().id, this.items).subscribe({
      next: (response) => {
        console.log('Cart updated successfully on server:', response);
      },
      error: (error) => {
        console.error('Failed to update cart on server:', error);
      }
    });
    localStorage.setItem('cartItems', JSON.stringify(this.items));
  }
}
