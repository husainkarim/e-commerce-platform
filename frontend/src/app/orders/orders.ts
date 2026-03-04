import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthServiceService } from '../auth-service.service';
import { ClientOrders } from '../client-orders/client-orders';
import { SellerOrders } from '../seller-orders/seller-orders';

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
  imports: [CommonModule, ClientOrders, SellerOrders],
  templateUrl: './orders.html',
  styleUrl: './orders.css',
})
export class Orders implements OnInit {
  isClient: boolean = false;
  isSeller: boolean = false;

  constructor(
    private readonly authService: AuthServiceService,
    private readonly router: Router
  ) {}

  ngOnInit() {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }

    const role = this.authService.getUser()?.role;
    this.isClient = role === 'client';
    this.isSeller = role === 'seller';

    if (!this.isClient && !this.isSeller) {
      this.router.navigate(['/unauthorized']);
    }
  }
}
