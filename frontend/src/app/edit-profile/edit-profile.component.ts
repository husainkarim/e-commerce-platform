import { Component, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormGroup, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthServiceService } from '../auth-service.service';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.css',
})
export class EditProfileComponent implements OnChanges {
  user: any = null;

  avatars = ['1.png','2.png','3.png','4.png','5.png','6.png','7.png','8.png','9.png'];

  profileForm: FormGroup;

  constructor(private authServiceService: AuthServiceService, private apiService: ApiService) {
    this.user = authServiceService.getUser();
    this.profileForm = new FormGroup({
      id: new FormControl({ value: this.user?.id ?? '', disabled: true }),
      email: new FormControl({ value: this.user?.email ?? '', disabled: true }),
      name: new FormControl(this.user?.name ?? '', [Validators.required, Validators.minLength(2)]),
      avatar: new FormControl(this.user?.avatar ?? this.avatars[0], [Validators.required]),
      password: new FormControl('')
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['user'] && this.user) {
      // Patch the form while keeping id and email disabled
      this.profileForm.patchValue({
        id: this.user.id ?? '',
        email: this.user.email ?? '',
        name: this.user.name ?? '',
        avatar: this.user.avatar ?? this.avatars[0],
        password: ''
      });
    }
  }

  onSave(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }
    this.user.name = this.profileForm.get('name')?.value;
    this.user.avatar = this.profileForm.get('avatar')?.value;
    const newPassword = this.profileForm.get('password')?.value;
    if (newPassword) {
      this.user.password = newPassword;
    }
    this.apiService.updateProfile(this.user.id, this.user).subscribe({
      next: (response) => {
        console.log(response);
        localStorage.setItem('user', JSON.stringify(this.user));
        this.authServiceService.setUser(this.user);},
      error: (error) => {
        console.error('Error updating profile:', error);
      }
    });
  }

  onRoleButton(): void {
    let updatedUser = {id: this.user.id, role: this.user.role === 'client' ? 'seller' : 'client'};
    this.apiService.updateRole(this.user.id, updatedUser).subscribe({
      next: (response) => {
        console.log(response);
        this.user.role = updatedUser.role;
        localStorage.setItem('user', JSON.stringify(this.user));
        this.authServiceService.setUser(this.user);
      },
      error: (error) => {
        console.error('Error changing user role:', error);
      }
    });
  }
}
