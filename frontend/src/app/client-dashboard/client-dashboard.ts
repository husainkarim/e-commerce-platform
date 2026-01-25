import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthServiceService } from '../auth-service.service';
import { ApiService } from '../api.service';
import { Orders } from '../orders/orders';
import { filter } from 'rxjs/operators';

interface MostBoughtProduct {
  id: number;
  name: string;
  quantity: number;
}

interface TopCategory {
  name: string;
  percentage: number;
  count: number;
}

interface RecentOrder {
  id: number;
  date: string;
  amount: number;
  status: string;
}

@Component({
  selector: 'app-client-dashboard',
  imports: [CommonModule],
  templateUrl: './client-dashboard.html',
  styleUrl: './client-dashboard.css',
})
export class ClientDashboard implements OnInit {
  userName: string = '';
  totalSpent: number = 0;
  totalOrders: number = 0;
  totalItemsPurchased: number = 0;
  items: any[] = [];
  setItems: any = {};
  setCategories: any = {};
  mostBoughtProducts: MostBoughtProduct[] = [];
  topCategories: TopCategory[] = [];
  recentOrders: RecentOrder[] = [];
  chartColors: string[] = [
    '#FF6384',
    '#36A2EB',
    '#FFCE56',
    '#4BC0C0',
    '#9966FF',
    '#FF9F40',
  ];

  constructor(private authService: AuthServiceService, private apiService: ApiService) {}

  ngOnInit() {
    const user = this.authService.getUser();
    this.userName = user?.name || user?.email || 'User';
    this.generateAnalytics();
  }

  generateAnalytics() {
    this.apiService.getClientData(this.authService.getUser().id).subscribe({
      next: (response) => {
        this.totalOrders = response.clientOrders.totalOrders;
        this.totalSpent = response.clientOrders.totalSpent;
        this.totalItemsPurchased = response.clientOrders.orders.map((order: any) => order.items.length).reduce((a: number, b: number) => a + b, 0);
        this.items = response.clientOrders.orders.flatMap((order: any) => order.items);
        for (let item of this.items) {
          this.setItems[item.productId] = (this.setItems[item.productId] || 0) + item.quantity;
          this.setCategories[item.category] = (this.setCategories[item.category] || 0) + item.quantity;
        }
        this.mostBoughtProducts = Object.entries(this.setItems).map(([id, quantity]) => ({
          id: Number(id),
          name: response.products.find((p: any) => p.id === Number(id))?.name || 'Unknown',
          quantity: quantity as number,
        })).sort((a, b) => b.quantity - a.quantity).slice(0, 4);
        this.topCategories = Object.entries(this.setCategories).map(([name, count]) => ({
          name,
          count: count as number,
          percentage: Math.round(((count as number) / this.totalItemsPurchased) * 100),
        })).sort((a, b) => b.count - a.count).slice(0, 5);
        this.recentOrders = response.clientOrders.orders.sort((a: any, b: any) => new Date(b.date).getTime() - new Date(a.date).getTime()).slice(0, 5).map((order: any) => ({
          id: order.id,
          date: order.date,
          amount: order.totalAmount,
          status: order.status,
        }));
      },
      error: (error) => {
        console.error('Failed to fetch client dashboard data:', error);
      }
    });
  }
}
