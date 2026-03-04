import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormGroup, ReactiveFormsModule, FormControl, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ApiService } from '../api.service';
import { AuthServiceService } from '../auth-service.service';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './product-form.component.html',
  styleUrl: './product-form.component.css'
})
export class ProductFormComponent implements OnInit {
  productForm: FormGroup;

  isEditMode = false;
  product = {
    id: '',
    name: '',
    description: '',
    price: 0,
    quantity: 0,
    userId: '',
    category: ''
  };

  categories: string[] = ['Automotive', 'Beauty', 'Books', 'Electronics', 'Fashion', 'Garden', 'General', 'Groceries', 'Health', 'Home', 'Jewelry', 'Movies', 'Music', 'Sports', 'Toys'];

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly apiService: ApiService,
    private readonly authServiceService: AuthServiceService
  ) {
    this.productForm = new FormGroup({
      name: new FormControl('', [Validators.required ]),
      description: new FormControl('', [Validators.required ]),
      price: new FormControl(0, [Validators.required ]),
      quantity: new FormControl(0, [Validators.required ]),
      category: new FormControl('General', [Validators.required ])
    });
  }

  ngOnInit(): void {
    if (!this.authServiceService.isLoggedIn()) {
      console.error('User is not logged in.');
      this.router.navigate(['/login']);
      return;
    }

    const id = this.route.snapshot.paramMap.get('id');
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
              quantity: this.product.quantity,
              category: this.product.category || 'General'
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
      userId: user.id,
      category: this.productForm.value.category
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
    this.router.navigate(['/profile']);
  }
}
