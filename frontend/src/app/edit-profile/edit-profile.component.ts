import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormGroup, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { Auth } from '@angular/fire/auth';
import { AuthServiceService } from '../auth-service.service';

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.css',
})
export class EditProfileComponent implements OnChanges {
  /** Input user object (parent should pass the current user) */
  user: any = null;

  /** Emitted when user saves their profile (name/avatar/password). Parent should persist changes. */
  @Output() save = new EventEmitter<{ id?: string | number; name?: string; email?: string; role?: string; avatar?: string; password?: string }>();

  /** Emitted when the role button is clicked so parent can handle role changes separately. */
  @Output() roleAction = new EventEmitter<void>();

  avatars = ['1.png','2.png','3.png','4.png','5.png','6.png','7.png','8.png','9.png'];

  profileForm: FormGroup;

  constructor(private authServiceService: AuthServiceService) {
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

    const raw = this.profileForm.getRawValue(); // includes disabled values
    const payload: any = {
      id: raw.id,
      email: raw.email,
      name: raw.name,
      avatar: raw.avatar
    };
    if (raw.password) {
      payload.password = raw.password;
    }

    // emit to parent to persist
    this.save.emit(payload);
  }

  onRoleButton(): void {
    this.roleAction.emit();
  }
}
