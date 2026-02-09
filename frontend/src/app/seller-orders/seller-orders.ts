import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../api.service';
import { AuthServiceService } from '../auth-service.service';
import { FormsModule } from '@angular/forms';
import { update } from '@angular/fire/database';

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

interface UpdateStatus {
  orderId: string;
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
    { value: 'PENDING', label: 'Pending' },
    { value: 'CONFIRMED', label: 'Confirmed' },
    { value: 'PROCESSED', label: 'Processed' },
    { value: 'DELIVERED', label: 'Delivered' },
    { value: 'CANCELLED', label: 'Cancelled' }
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
        this.orders = data.orders;
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

  getTotalQuantity(items: any[]): number {
    return items.reduce((acc, item) => acc + item.quantity, 0);
  }

  acceptOrder(orderId: string): void {
    if (confirm('Are you sure you want to accept this order?')) {
      this.updateOrderStatus(orderId, 'CONFIRMED');
    }
  }

  rejectOrder(orderId: string): void {
    if (confirm('Are you sure you want to reject this order?')) {
      this.updateOrderStatus(orderId, 'CANCELLED');
    }
  }

  markAsProcessing(orderId: string): void {
    this.updateOrderStatus(orderId, 'PROCESSED');
  }

  markAsDelivered(orderId: string): void {
    if (confirm('Confirm that this order has been delivered?')) {
      this.updateOrderStatus(orderId, 'DELIVERED');
    }
  }

  cancelOrder(orderId: string): void {
    if (confirm('Are you sure you want to cancel this order?')) {
      this.updateOrderStatus(orderId, 'CANCELLED');
    }
  }

  updateOrderStatus(orderId: string, newStatus: string): void {
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

  canDeliver(status: string): boolean {
    return status === 'PROCESSED';
  }

  canCancel(status: string): boolean {
    return ['PENDING', 'CONFIRMED', 'PROCESSED'].includes(status);
  }
}
