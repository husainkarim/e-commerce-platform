import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthServiceService } from '../auth-service.service';

interface Order {
  id: number;
  orderNumber: string;
  date: string;
  status: string;
  total: number;
  itemsCount: number;
}

@Component({
  selector: 'app-orders',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './orders.html',
  styleUrl: './orders.css',
})
export class Orders implements OnInit {
  orders: Order[] = [];
  filteredOrders: Order[] = [];
  searchStatus: string = '';
  searchDate: string = '';
  statusOptions = ['All', 'Pending', 'Shipped', 'Delivered', 'Cancelled'];

  constructor(
    private authService: AuthServiceService,
    private router: Router
  ) {}

  ngOnInit() {
    // if (!this.authService.isLoggedIn()) {
    //   this.router.navigate(['/login']);
    //   return;
    // }
    this.loadOrders();
  }

  loadOrders() {
    // Sample order data (in real app, would come from backend API)
    this.orders = [
      {
        id: 1,
        orderNumber: 'ORD-001',
        date: '2024-01-02',
        status: 'Delivered',
        total: 120.96,
        itemsCount: 3,
      },
      {
        id: 2,
        orderNumber: 'ORD-002',
        date: '2024-01-05',
        status: 'Delivered',
        total: 120.98,
        itemsCount: 1,
      },
      {
        id: 3,
        orderNumber: 'ORD-003',
        date: '2024-01-12',
        status: 'Shipped',
        total: 179.97,
        itemsCount: 2,
      },
      {
        id: 4,
        orderNumber: 'ORD-004',
        date: '2024-01-18',
        status: 'Pending',
        total: 76.98,
        itemsCount: 2,
      },
      {
        id: 5,
        orderNumber: 'ORD-005',
        date: '2023-12-28',
        status: 'Delivered',
        total: 299.97,
        itemsCount: 4,
      },
      {
        id: 6,
        orderNumber: 'ORD-006',
        date: '2023-12-20',
        status: 'Cancelled',
        total: 102.99,
        itemsCount: 1,
      },
    ];

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
