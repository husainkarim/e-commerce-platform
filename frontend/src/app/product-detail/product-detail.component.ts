import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.css'
})
export class ProductDetailComponent {
  product: any;

  // Example static products data (replace with service/API in real app)
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

  constructor(private route: ActivatedRoute) {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.product = this.products.find(p => p.id === id);
  }
}
