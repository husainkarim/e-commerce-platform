import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './products.component.html',
  styleUrl: './products.component.css'
})
export class ProductsComponent {
  products = [
    {
      id: 1,
      name: 'Wireless Headphones',
      description: 'High-quality wireless headphones with noise cancellation.',
      price: 99.99,
      quantity: 10,
      userId: 'seller1',
      image: 'https://via.placeholder.com/150'
    },
    {
      id: 2,
      name: 'Smart Watch',
      description: 'Feature-rich smart watch with health tracking.',
      price: 149.99,
      quantity: 5,
      userId: 'seller2',
      image: 'https://via.placeholder.com/150'
    },
    {
      id: 3,
      name: 'Bluetooth Speaker',
      description: 'Portable Bluetooth speaker with deep bass.',
      price: 59.99,
      quantity: 20,
      userId: 'seller3',
      image: 'https://via.placeholder.com/150'
    }
  ];
}
