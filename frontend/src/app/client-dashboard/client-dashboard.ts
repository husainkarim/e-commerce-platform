import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthServiceService } from '../auth-service.service';

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

  constructor(private authService: AuthServiceService) {}

  ngOnInit() {
    const user = this.authService.getUser();
    this.userName = user?.name || user?.email || 'User';
    this.generateAnalytics();
  }

  generateAnalytics() {
    // Sample purchase data (in real app, would come from backend order API)
    const sampleProducts = [
      { id: 1, name: 'Wireless Headphones', quantity: 3, category: 'Electronics', price: 79.99 },
      { id: 2, name: 'Running Shoes', quantity: 2, category: 'Fashion', price: 89.99 },
      { id: 3, name: 'Coffee Maker', quantity: 1, category: 'Home', price: 129.99 },
      { id: 4, name: 'T-Shirt', quantity: 5, category: 'Fashion', price: 24.99 },
      { id: 5, name: 'Phone Case', quantity: 2, category: 'Electronics', price: 14.99 },
      { id: 6, name: 'Yoga Mat', quantity: 1, category: 'Sports', price: 34.99 },
    ];

    // Calculate metrics
    this.totalItemsPurchased = sampleProducts.reduce((sum, p) => sum + p.quantity, 0);
    this.totalSpent = sampleProducts.reduce((sum, p) => sum + (p.quantity * p.price), 0);
    this.totalOrders = Math.floor(this.totalItemsPurchased / 3) + 1;

    // Most bought products (top 4)
    this.mostBoughtProducts = sampleProducts
      .map(p => ({ id: p.id, name: p.name, quantity: p.quantity }))
      .sort((a, b) => b.quantity - a.quantity)
      .slice(0, 4);

    // Top categories
    const categoryMap = new Map<string, number>();
    sampleProducts.forEach(p => {
      const current = categoryMap.get(p.category) || 0;
      categoryMap.set(p.category, current + p.quantity);
    });

    const categoryEntries = Array.from(categoryMap.entries())
      .sort((a, b) => b[1] - a[1])
      .slice(0, 5);

    const totalCategoryItems = categoryEntries.reduce((sum, [_, count]) => sum + count, 0);

    this.topCategories = categoryEntries.map(([name, count]) => ({
      name,
      count,
      percentage: Math.round((count / totalCategoryItems) * 100),
    }));

    // Generate recent orders
    this.recentOrders = [
      { id: 3001, date: '2 days ago', amount: 134.98, status: 'Delivered' },
      { id: 3002, date: '5 days ago', amount: 89.99, status: 'Delivered' },
      { id: 3003, date: '1 week ago', amount: 179.97, status: 'Delivered' },
      { id: 3004, date: '2 weeks ago', amount: 49.98, status: 'Delivered' },
    ];
  }
}
