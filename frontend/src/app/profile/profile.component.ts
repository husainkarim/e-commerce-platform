import { Component } from '@angular/core';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent {
  user = {
    id: 'u123',
    name: 'Jane Doe',
    email: 'jane.doe@example.com',
    role: 'Seller',
    avatar: 'assets/avatars/1.png'
  };
}
