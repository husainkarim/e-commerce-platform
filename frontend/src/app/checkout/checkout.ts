import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthServiceService } from '../auth-service.service';
import { ApiService } from '../api.service';

interface CartItem {
  id: string;
  name: string;
  image: string;
  price: number;
  quantity: number;
}

interface Address {
  fullName: string;
  email: string;
  phone: string;
  address: string;
  city: string;
  country: string;
  notes: string;
}

interface Order {
  userId: string;
  items: CartItem[];
  shippingAddress: Address;
  shippingFees: number;
  paymentMethod: string;
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
  shippingFee = 0;
  paymentMethod = 'PAY ON DELIVERY';
  shippingAddress: Address = {
    fullName: '',
    email: '',
    phone: '',
    address: '',
    city: '',
    country: '',
    notes: '',
  };

  constructor(private authService: AuthServiceService, private router: Router, private apiService: ApiService) {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }
    this.shippingAddress.fullName = this.authService.getUser().name;
    this.shippingAddress.email = this.authService.getUser().email;
    this.loadCart();
  }

  get subtotal(): number {
    return this.items.reduce((sum, item) => sum + item.price * item.quantity, 0);
  }

  get shipping(): number {
    return this.subtotal < 100 ? this.shippingFee = 20 : this.shippingFee = 0;
  }

  get total(): number {
    return this.subtotal + this.shippingFee;
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
    const order: Order = {
      userId: this.authService.getUser().id,
      items: this.items,
      shippingAddress: this.shippingAddress,
      shippingFees: this.shipping,
      paymentMethod: this.paymentMethod,
    };
    this.apiService.placeOrder(this.authService.getUser().id, order).subscribe({
      next: (response) => {
        console.log('Order placed successfully:', response);
        localStorage.removeItem('cartItems');
        this.items = [];
      },
      error: (error) => {
        console.error('Failed to place order:', error);
      }
    });
  }

  private isAddressValid(): boolean {
    const required = [
      this.shippingAddress.fullName,
      this.shippingAddress.email,
      this.shippingAddress.phone,
      this.shippingAddress.address,
      this.shippingAddress.city,
      this.shippingAddress.country,
    ];
    return required.every((field) => field.trim().length > 0);
  }

  private loadCart(): void {
    const stored = localStorage.getItem('cartItems');
    if (stored) {
      try {
        this.apiService.getCartByUserId(this.authService.getUser().id).subscribe({
          next: (response) => {
            this.items = response.items || [];
          }
        });
      } catch (error) {
        console.warn('Falling back to sample checkout items because stored data is invalid.', error);
      }
    }
  }
}
