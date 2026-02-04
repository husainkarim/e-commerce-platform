import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthServiceService } from '../auth-service.service';
import { ApiService } from '../api.service';
import { forkJoin } from 'rxjs';

interface CartItem {
  sellerId: string;
  productId: string;
  productName: string;
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
  status: string;
  shippingAddress: Address;
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
  products: any[] = [];
  confirmed = false;
  items: CartItem[] = [];
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

  get total(): number {
    return this.subtotal;
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
    console.log('Confirmed order items:', this.items);
    const order: Order = {
      userId: this.authService.getUser().id,
      items: this.items,
      status: 'PENDING',
      shippingAddress: this.shippingAddress,
      paymentMethod: this.paymentMethod,
    };
    this.apiService.placeOrder(this.authService.getUser().id, order).subscribe({
      next: (response) => {
        console.log('Order placed successfully:', response.message);
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
    const stored = JSON.parse(localStorage.getItem('cartItems') || 'null');

    if (!stored) {
      console.log('No cart items found in localStorage');
      return;
    }

    this.items = stored;
    this.products = [];

    this.items.forEach(item => {
      forkJoin({
        productRes: this.apiService.getProductById(item.productId),
        imageRes: this.apiService.getImagesByProductId(item.productId)
      }).subscribe({
        next: ({ productRes, imageRes }) => {

          // update price from backend (important!)
          item.price = productRes.product.price;

          // resolve image
          const imageUrl =
            imageRes?.images?.length > 0
              ? imageRes.images[0].imagePath
              : 'assets/product-images/default-product-image.jpg';

          productRes.product.imageUrl = imageUrl;

          this.products.push(productRes.product);

          console.log('Loaded product data for cart item:', productRes.product);
        },
        error: (err) => {
          console.error('Failed to load product or image:', err);
        }
      });
    });

    console.log('Loaded cart items:', this.items);
  }
}
