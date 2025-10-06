import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './product-form.component.html',
  styleUrl: './product-form.component.css'
})
export class ProductFormComponent {
  isEditMode = false;
  product = {
    name: '',
    description: '',
    price: 0,
    quantity: 0,
    userId: ''
  };

  // Example static products data (replace with service/API in real app)
  products = [
    {
      id: 1,
      name: 'Wireless Headphones',
      description: 'High-quality wireless headphones with noise cancellation.',
      price: 99.99,
      quantity: 10,
      userId: 'seller1'
    },
    {
      id: 2,
      name: 'Smart Watch',
      description: 'Feature-rich smart watch with health tracking.',
      price: 149.99,
      quantity: 5,
      userId: 'seller2'
    }
  ];

  constructor(private route: ActivatedRoute) {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      const productId = Number(id);
      const found = this.products.find((p: any) => p.id === productId);
      if (found) {
        this.product = { ...found };
      }
    }
  }

  onSubmit() {
    if (this.isEditMode) {
      // Update product logic
      alert('Product updated!');
    } else {
      // Create product logic
      alert('Product created!');
    }
  }
}
