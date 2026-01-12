import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthServiceService } from '../auth-service.service';

interface OrderItem {
  id: number;
  name: string;
  quantity: number;
  price: number;
  image: string;
}

interface TimelineEvent {
  status: string;
  date: string;
  time: string;
  description: string;
  completed: boolean;
}

interface OrderDetailData {
  id: number;
  orderNumber: string;
  orderDate: string;
  status: string;
  items: OrderItem[];
  subtotal: number;
  shipping: number;
  total: number;
  shippingAddress: {
    name: string;
    street: string;
    city: string;
    state: string;
    zip: string;
    phone: string;
  };
  timeline: TimelineEvent[];
}

@Component({
  selector: 'app-order-detail',
  imports: [CommonModule, RouterModule],
  templateUrl: './order-detail.html',
  styleUrl: './order-detail.css',
})
export class OrderDetail implements OnInit {
  order: OrderDetailData | null = null;
  loading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthServiceService
  ) {}

  ngOnInit() {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }

    this.route.params.subscribe((params) => {
      const orderId = params['id'];
      this.loadOrderDetails(orderId);
    });
  }

  loadOrderDetails(orderId: number) {
    // Sample order data (in real app, would come from backend API)
    this.loading = true;

    setTimeout(() => {
      if (orderId == 1) {
        this.order = {
          id: 1,
          orderNumber: 'ORD-001',
          orderDate: '2024-01-02',
          status: 'Delivered',
          items: [
            {
              id: 1,
              name: 'Wireless Headphones',
              quantity: 1,
              price: 79.99,
              image: 'assets/product-images/headphones.jpg',
            },
            {
              id: 2,
              name: 'Phone Case',
              quantity: 2,
              price: 14.99,
              image: 'assets/product-images/case.jpg',
            },
          ],
          subtotal: 109.97,
          shipping: 0.00,
          total: 120.96,
          shippingAddress: {
            name: 'John Doe',
            street: '123 Main Street',
            city: 'New York',
            state: 'NY',
            zip: '10001',
            phone: '+1 (555) 123-4567',
          },
          timeline: [
            {
              status: 'Order Placed',
              date: '2024-01-02',
              time: '10:30 AM',
              description: 'Your order has been confirmed',
              completed: true,
            },
            {
              status: 'Processing',
              date: '2024-01-02',
              time: '2:15 PM',
              description: 'Your order is being prepared',
              completed: true,
            },
            {
              status: 'Shipped',
              date: '2024-01-03',
              time: '8:45 AM',
              description: 'Shipment is on the way',
              completed: true,
            },
            {
              status: 'Delivered',
              date: '2024-01-05',
              time: '3:20 PM',
              description: 'Package delivered to your address',
              completed: true,
            },
          ],
        };
      } else {
        this.order = {
          id: 2,
          orderNumber: 'ORD-002',
          orderDate: '2024-01-05',
          status: 'Shipped',
          items: [
            {
              id: 3,
              name: 'Running Shoes',
              quantity: 1,
              price: 89.99,
              image: 'assets/product-images/shoes.jpg',
            },
          ],
          subtotal: 89.99,
          shipping: 20.00,
          total: 120.98,
          shippingAddress: {
            name: 'Jane Smith',
            street: '456 Oak Avenue',
            city: 'Los Angeles',
            state: 'CA',
            zip: '90001',
            phone: '+1 (555) 987-6543',
          },
          timeline: [
            {
              status: 'Order Placed',
              date: '2024-01-05',
              time: '1:00 PM',
              description: 'Your order has been confirmed',
              completed: true,
            },
            {
              status: 'Processing',
              date: '2024-01-05',
              time: '3:30 PM',
              description: 'Your order is being prepared',
              completed: true,
            },
            {
              status: 'Shipped',
              date: '2024-01-06',
              time: '9:00 AM',
              description: 'Shipment is on the way',
              completed: true,
            },
            {
              status: 'Delivered',
              date: 'TBD',
              time: 'Pending',
              description: 'Arriving soon',
              completed: false,
            },
          ],
        };
      }

      this.loading = false;
    }, 500);
  }

  goBack() {
    this.router.navigate(['/orders']);
  }
}
