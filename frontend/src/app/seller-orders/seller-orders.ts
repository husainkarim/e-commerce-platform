import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../api.service';
import { AuthServiceService } from '../auth-service.service';
import { FormsModule } from '@angular/forms';
import { update } from '@angular/fire/database';

interface Order {
  id: number;
  orderNumber: string;
  customerName: string;
  customerEmail: string;
  productName: string;
  quantity: number;
  totalPrice: number;
  status: string;
  orderDate: string;
  shippingAddress: string;
}

interface UpdateStatus {
  orderId: number;
  status: string;
}

@Component({
  selector: 'app-seller-orders',
  imports: [CommonModule, FormsModule],
  templateUrl: './seller-orders.html',
  styleUrls: ['./seller-orders.css'],
})
export class SellerOrders implements OnInit {
  orders: Order[] = [];
  filteredOrders: Order[] = [];
  selectedStatus: string = 'all';
  loading: boolean = false;
  error: string = '';

  statusOptions = [
    { value: 'all', label: 'All Orders' },
    { value: 'pending', label: 'Pending' },
    { value: 'accepted', label: 'Accepted' },
    { value: 'processing', label: 'Processing' },
    { value: 'shipped', label: 'Shipped' },
    { value: 'delivered', label: 'Delivered' },
    { value: 'cancelled', label: 'Cancelled' },
    { value: 'rejected', label: 'Rejected' }
  ];

  constructor(
    private apiService: ApiService,
    private authService: AuthServiceService
  ) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading = true;
    this.error = '';

    // Uncomment this when API is ready:
    this.apiService.getSellerOrders(this.authService.getUser().id).subscribe({
      next: (data) => {
        this.orders = data;
        this.filterOrders();
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load orders. Please try again.';
        this.loading = false;
      }
    });
  }

  filterOrders(): void {
    if (this.selectedStatus === 'all') {
      this.filteredOrders = [...this.orders];
    } else {
      this.filteredOrders = this.orders.filter(
        order => order.status === this.selectedStatus
      );
    }
  }

  onStatusFilterChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.selectedStatus = target.value;
    this.filterOrders();
  }

  acceptOrder(orderId: number): void {
    if (confirm('Are you sure you want to accept this order?')) {
      this.updateOrderStatus(orderId, 'accepted');
    }
  }

  rejectOrder(orderId: number): void {
    if (confirm('Are you sure you want to reject this order? This action cannot be undone.')) {
      this.updateOrderStatus(orderId, 'rejected');
    }
  }

  markAsProcessing(orderId: number): void {
    this.updateOrderStatus(orderId, 'processing');
  }

  markAsShipped(orderId: number): void {
    if (confirm('Confirm that this order has been shipped?')) {
      this.updateOrderStatus(orderId, 'shipped');
    }
  }

  markAsDelivered(orderId: number): void {
    if (confirm('Confirm that this order has been delivered?')) {
      this.updateOrderStatus(orderId, 'delivered');
    }
  }

  cancelOrder(orderId: number): void {
    if (confirm('Are you sure you want to cancel this order?')) {
      this.updateOrderStatus(orderId, 'cancelled');
    }
  }

  updateOrderStatus(orderId: number, newStatus: string): void {
    this.loading = true;

    // Mock update - replace with actual API call
    setTimeout(() => {
      const orderIndex = this.orders.findIndex(o => o.id === orderId);
      if (orderIndex !== -1) {
        this.orders[orderIndex].status = newStatus;
        this.filterOrders();
      }
      this.loading = false;
    }, 300);
    let updateState: UpdateStatus = { orderId, status: newStatus };
    this.apiService.updateOrderStatus(this.authService.getUser().id, updateState).subscribe({
      next: (response) => {
        const orderIndex = this.orders.findIndex(o => o.id === orderId);
        if (orderIndex !== -1) {
          this.orders[orderIndex].status = newStatus;
          this.filterOrders();
        }
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to update order status. Please try again.';
        this.loading = false;
      }
    });
  }
  // PENDING|CONFIRMED|PROCESSED|DELIVERED|CANCELLED
  getStatusClass(status: string): string {
    const statusClasses: { [key: string]: string } = {
      'PENDING': 'status-pending',
      'CONFIRMED': 'status-confirmed',
      'PROCESSED': 'status-processed',
      'DELIVERED': 'status-delivered',
      'CANCELLED': 'status-cancelled',
    };
    return statusClasses[status] || '';
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  canAccept(status: string): boolean {
    return status === 'PENDING';
  }

  canReject(status: string): boolean {
    return status === 'PENDING';
  }

  canProcess(status: string): boolean {
    return status === 'CONFIRMED';
  }

  canShip(status: string): boolean {
    return status === 'PROCESSED';
  }

  canDeliver(status: string): boolean {
    return status === 'DELIVERED';
  }

  canCancel(status: string): boolean {
    return ['PENDING', 'CONFIRMED', 'PROCESSED'].includes(status);
  }
}
