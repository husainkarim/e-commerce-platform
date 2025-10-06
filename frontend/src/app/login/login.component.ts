import { Component } from '@angular/core';
import {FormGroup, FormControl, ReactiveFormsModule, Validators} from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginForm: FormGroup;

  constructor( private router: Router, private apiService: ApiService ) {
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required, Validators.minLength(8)])
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      console.log(this.loginForm.value);

      //TODO: send post to backend API
      this.apiService.login(this.loginForm.value).subscribe({
        next: (response) => {
          console.log('Login successful:', response);
          // Handle successful login (e.g., store token, redirect)
        },
        error: (error) => {
          console.error('Login failed:', error);
          // Handle login error (e.g., show error message)
        }
      });

      // redirect to dashboard or home page
      this.router.navigate(['/']);
      this.loginForm.reset();
    }
  }

}
