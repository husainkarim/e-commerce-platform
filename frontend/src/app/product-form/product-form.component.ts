import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormGroup, ReactiveFormsModule, FormControl, Validators } from '@angular/forms';
import { ApiService } from '../api.service';
import { AuthServiceService } from '../auth-service.service';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './product-form.component.html',
  styleUrl: './product-form.component.css'
})
export class ProductFormComponent {
  productForm: FormGroup;

  isEditMode = false;
  product = {
    id: '',
    name: '',
    description: '',
    price: 0,
    quantity: 0,
    userId: ''
  };

  // Example static products data (replace with service/API in real app)
  products = [
    {
      id: '1',
      name: 'Wireless Headphones',
      description: 'High-quality wireless headphones with noise cancellation.',
      price: 99.99,
      quantity: 10,
      userId: 'seller1'
    },
    {
      id: '2',
      name: 'Smart Watch',
      description: 'Feature-rich smart watch with health tracking.',
      price: 149.99,
      quantity: 5,
      userId: 'seller2'
    }
  ];

  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService, private authServiceService: AuthServiceService) {
    const id = this.route.snapshot.paramMap.get('id');
    this.productForm = new FormGroup({
      name: new FormControl('', [Validators.required ]),
      description: new FormControl('', [Validators.required ]),
      price: new FormControl(0, [Validators.required ]),
      quantity: new FormControl(0, [Validators.required ]),
    });

    if (id) {
      this.isEditMode = true;
      this.apiService.getProductById(id).subscribe({
        next: (response) => {
          const prod = response.product;
          if (prod) {
            this.product = { ...prod };
            this.productForm.setValue({
              name: this.product.name,
              description: this.product.description,
              price: this.product.price,
              quantity: this.product.quantity
            });
          }
        },
        error: (error) => {
          console.error('Failed to fetch products:', error);
          this.router.navigate(['/not-found']);
        }
      });
    }
  }

  onSubmit() {
    const user = this.authServiceService.getUser();
    this.product = {
      id: this.isEditMode ? this.product.id : '',
      name: this.productForm.value.name,
      description: this.productForm.value.description,
      price: this.productForm.value.price,
      quantity: this.productForm.value.quantity,
      userId: user.id
    };
    if (this.isEditMode) {
      // Update product logic
      this.apiService.updateProduct(this.product.id, this.product).subscribe({
        next: (response) => {
          console.log('Product updated successfully:', response);
          alert('Product updated!');
        },
        error: (error) => {
          console.error('Failed to update product:', error);
        }
      });
    } else {
      // Create product logic
      this.apiService.createProduct(this.product).subscribe({
        next: (response) => {
          console.log('Product created successfully:', response);
          alert('Product created!');
        },
        error: (error) => {
          console.error('Failed to create product:', error);
        }
      });
    }
  }
}
