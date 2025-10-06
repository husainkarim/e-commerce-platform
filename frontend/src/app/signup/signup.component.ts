import { Component } from '@angular/core';
import { FormGroup, FormControl, ReactiveFormsModule, Validators, AbstractControl, ValidatorFn, ValidationErrors } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ApiService } from '../api.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.css'
})
export class SignupComponent {
  signupForm: FormGroup;
  roles = {
    options: ['client', 'seller']
  };
  avatars = {
    options: ['1.png', '2.png', '3.png', '4.png', '5.png', '6.png', '7.png', '8.png', '9.png']
  };

  constructor( private router: Router, private apiService: ApiService ) {
    this.signupForm = new FormGroup({
      name: new FormControl('', [Validators.required, Validators.minLength(4)]),
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required, Validators.minLength(8)]),
      confirmPassword: new FormControl('', [Validators.required]),
      role: new FormControl('client', [Validators.required]),
      avatar: new FormControl('1.png', [Validators.required])
    }, { validators: passwordMatchValidator });
  }

  onSubmit() {
    if (this.signupForm.valid) {
      console.log(this.signupForm.value);

      // TODO: send post to backend API
      this.apiService.signup(this.signupForm.value).subscribe({
        next: (response) => {
          console.log('Signup successful:', response);
          // Handle successful signup (e.g., store token, redirect)
        },
        error: (error) => {
          console.error('Signup failed:', error);
          // Handle signup error (e.g., show error message)
        }
      });

      // redirect to dashboard or home page
      this.router.navigate(['/']);
      this.signupForm.reset();
    }
  }
}

export const passwordMatchValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const password = control.get('password')?.value;
  const confirmPassword = control.get('confirmPassword')?.value;
  return password === confirmPassword ? null : { passwordMismatch: true };
};
