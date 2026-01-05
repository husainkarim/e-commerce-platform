import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

interface CartItem {
  id: string;
  name: string;
  image: string;
  price: number;
  quantity: number;
}

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './cart.html',
  styleUrl: './cart.css',
})
export class Cart {
  items: CartItem[] = [];

  constructor() {
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

  trackById(_: number, item: CartItem): string {
    return item.id;
  }

  private loadCart(): void {
    const stored = localStorage.getItem('cartItems');
    if (stored) {
      try {
        //TODO
        this.items = JSON.parse(stored) as CartItem[];
        return;
      } catch (error) {
        console.warn('Falling back to sample cart items because stored data is invalid.', error);
      }
    }

    this.items = this.sampleItems();
    this.persistCart();
  }

  private persistCart(): void {
    //TODO
    localStorage.setItem('cartItems', JSON.stringify(this.items));
  }

  private sampleItems(): CartItem[] {
    return [
      {
        id: 'sample-1',
        name: 'Wireless Headphones',
        image: 'assets/product-images/default-product-image.jpg',
        price: 129.99,
        quantity: 1,
      },
      {
        id: 'sample-2',
        name: 'Smart Watch',
        image: 'assets/product-images/default-product-image.jpg',
        price: 199.0,
        quantity: 2,
      },
      {
        id: 'sample-3',
        name: 'Leather Backpack',
        image: 'assets/product-images/default-product-image.jpg',
        price: 89.5,
        quantity: 1,
      },
    ];
  }
}
