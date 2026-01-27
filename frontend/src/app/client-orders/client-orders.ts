import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthServiceService } from '../auth-service.service';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../api.service';

interface Order {
  id: number;
  orderNumber: string;
  date: string;
  status: string;
  total: number;
  itemsCount: number;
}

@Component({
  selector: 'app-client-orders',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './client-orders.html',
  styleUrl: './client-orders.css',
})
export class ClientOrders implements OnInit {
  orders: Order[] = [];
    filteredOrders: Order[] = [];
    searchStatus: string = '';
    searchDate: string = '';
    statusOptions = ['All', 'Pending', 'Shipped', 'Delivered', 'Cancelled'];

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
          this.orders = data;
        },
        error: (err) => {
          console.error('Error fetching orders:', err);
        }
      });

      this.applyFilters();
    }

    applyFilters() {
      this.filteredOrders = this.orders.filter((order) => {
        const statusMatch =
          !this.searchStatus ||
          this.searchStatus === 'All' ||
          order.status === this.searchStatus;

        const dateMatch =
          !this.searchDate || order.date === this.searchDate;

        return statusMatch && dateMatch;
      });
    }

    clearFilters() {
      this.searchStatus = '';
      this.searchDate = '';
      this.applyFilters();
    }

    viewOrderDetails(orderId: number) {
      this.router.navigate(['/orders', orderId]);
    }
}
