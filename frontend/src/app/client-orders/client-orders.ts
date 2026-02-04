import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthServiceService } from '../auth-service.service';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../api.service';

interface Order {
  id: string;
  createdAt: string;
  status: string;
  paymentMethod: string;
  shippingAddress: {
    fullName: string;
    email: string;
    phone: string;
    address: string;
    city: string;
    country: string;
    notes: string;
  };
  totalAmount: number;
  itemsCount: number;
  items: any[];
}

@Component({
  selector: 'app-client-orders',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './client-orders.html',
  styleUrls: ['./client-orders.css'],
})
export class ClientOrders implements OnInit {
  orders: Order[] = [];
    filteredOrders: Order[] = [];
    searchStatus: string = 'All';
    searchDate: string = '';
    // PENDING|CONFIRMED|PROCESSED|DELIVERED|CANCELLED
    statusOptions = ['All', 'PENDING', 'CONFIRMED', 'PROCESSED', 'DELIVERED', 'CANCELLED'];

    constructor(
      private authService: AuthServiceService,
      private apiService: ApiService,
      private router: Router
    ) {}

    ngOnInit() {
      if (!this.authService.isLoggedIn()) {
        this.router.navigate(['/login']);
        return;
      }
      this.loadOrders();
    }

    loadOrders() {
      this.apiService.getOrdersByUserId(this.authService.getUser().id).subscribe({
        next: (data) => {
          this.orders = data.orders;
          this.applyFilters();        },
        error: (err) => {
          console.error('Error fetching orders:', err);
        }
      });
    }

    applyFilters() {
      this.filteredOrders = this.orders.filter(order => {
        const statusMatch =
          !this.searchStatus ||
          this.searchStatus === 'All' ||
          order.status === this.searchStatus;

        const dateMatch =
          !this.searchDate ||
          new Date(order.createdAt).toISOString().split('T')[0] === this.searchDate;

        return statusMatch && dateMatch;
      });
    }

    clearFilters() {
      this.searchStatus = 'All';
      this.searchDate = '';
      this.applyFilters();
    }

    viewOrderDetails(orderId: string) {
      this.router.navigate(['/orders', orderId]);
    }
}
