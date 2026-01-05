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

interface Address {
  fullName: string;
  phone: string;
  street: string;
  city: string;
  state: string;
  zip: string;
  notes: string;
}

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './checkout.html',
  styleUrl: './checkout.css',
})
export class Checkout {
  step = 0;
  confirmed = false;
  items: CartItem[] = [];
  address: Address = {
    fullName: '',
    phone: '',
    street: '',
    city: '',
    state: '',
    zip: '',
    notes: '',
  };

  constructor() {
    this.loadCart();
  }

  get subtotal(): number {
    return this.items.reduce((sum, item) => sum + item.price * item.quantity, 0);
  }

  get shipping(): number {
    return this.subtotal < 100 ? 20 : 0;
  }

  get total(): number {
    return this.subtotal + this.shipping;
  }

  goTo(step: number): void {
    this.step = Math.min(Math.max(step, 0), 2);
  }

  next(): void {
    if (this.step === 0 && !this.isAddressValid()) {
      return;
    }
    if (this.step < 2) {
      this.step += 1;
    }
  }

  back(): void {
    if (this.step > 0) {
      this.step -= 1;
    }
  }

  confirm(): void {
    if (!this.isAddressValid()) {
      this.step = 0;
      return;
    }
    this.confirmed = true;
    this.step = 2;
  }

  private isAddressValid(): boolean {
    const required = [
      this.address.fullName,
      this.address.phone,
      this.address.street,
      this.address.city,
      this.address.state,
      this.address.zip,
    ];
    return required.every((field) => field.trim().length > 0);
  }

  private loadCart(): void {
    const stored = localStorage.getItem('cartItems');
    if (stored) {
      try {
        this.items = JSON.parse(stored) as CartItem[];
        if (this.items.length) {
          //TODO
          return;
        }
      } catch (error) {
        console.warn('Falling back to sample checkout items because stored data is invalid.', error);
      }
    }

    this.items = [
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
    ];
  }
}
