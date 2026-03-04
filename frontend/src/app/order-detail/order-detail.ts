import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthServiceService } from '../auth-service.service';
import { ApiService } from '../api.service';

interface OrderItem {
  productId: number;
  productName: string;
  quantity: number;
  price: number;
  image: string;
}

interface TimelineEvent {
  status: string;
  updatedAt: string;
  completed: boolean;
}

interface OrderDetailData {
  id: string;
  userId: string;
  sellerId: string;
  createdAt: string;
  updatedAt: string;
  status: string;
  items: OrderItem[];
  paymentMethod: string;
  totalAmount: number;
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
  styleUrls: ['./order-detail.css'],
})
export class OrderDetail implements OnInit {
  order: OrderDetailData | null = null;
  loading: boolean = true;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly authService: AuthServiceService,
    private readonly apiService: ApiService
  ) {}

  ngOnInit() {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }
    const id = this.route.snapshot.paramMap.get('id');
    if (id) { this.loadOrderDetails(id); }
  }

  loadOrderDetails(orderId: string) {
    // Sample order data (in real app, would come from backend API)
    this.loading = true;
    this.apiService.getOrderById(orderId, this.authService.getUser().id).subscribe({
      next: (data) => {
        console.log('Order data received:', data);
        this.order = data.order;
        if (this.order) {
          this.order.timeline = data.status;
        }
        this.order?.items.forEach(item => {
          this.apiService.getImagesByProductId(item.productId.toString()).subscribe({
            next: (imagesData) => {
              if (imagesData.images && imagesData.images.length > 0) {
                item.image = imagesData.images[0].imagePath;
              } else {
                item.image = 'assets/product-images/default-product-image.jpg'; // Fallback image
              }
            },
            error: (err) => {
              console.error('Error fetching product images:', err);
              item.image = 'assets/product-images/default-product-image.jpg'; // Fallback image
            }
          });
        });
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching order details:', err);
        this.loading = false;
      }
    });
  }


  goBack() {
    this.router.navigate(['/orders']);
  }
}
