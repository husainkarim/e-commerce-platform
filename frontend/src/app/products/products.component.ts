import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ApiService } from '../api.service';

interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  quantity: number;
  userId: string;
  image: string;
  // add other product fields as necessary
}

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css']
})
export class ProductsComponent {

  products: Product[] = [];

  constructor(private apiService: ApiService) {}

  private fetchProducts() {
    this.apiService.getProducts().subscribe({
      next: (response) => {
        console.log('Products fetched successfully:', response);
        this.products = response.products; // assuming response has a 'products' field
        for (let product of this.products) {
          this.apiService.getImagesByProductId(product.id).subscribe({
            next: (imageResponse) => {
              if (imageResponse.images && imageResponse.images.length > 0) {
                product.image = imageResponse.images[0].imagePath; // assuming images is an array of image URLs
              } else {
                product.image = 'assets/product-images/default-product-image.jpg'; // default image if none found
              }
            },
            error: (error) => {
              product.image = 'assets/product-images/default-product-image.jpg'; // default image on error
            }
          });
        }
      },
      error: (error) => {
        console.error('Failed to fetch products:', error);
      }
    });
  }

  ngOnInit() {
    this.fetchProducts();
  }

}
