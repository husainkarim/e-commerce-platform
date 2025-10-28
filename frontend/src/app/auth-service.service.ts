import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthServiceService {
  private user: any = null;
  private token: string | null = null;

  constructor() {
    const savedUser = localStorage.getItem('user');
    const savedToken = localStorage.getItem('authToken');
    if (savedUser) {
      this.user = JSON.parse(savedUser);
    }
    if (savedToken) {
      this.token = savedToken;
    }
  }

  login(userData: any) {
    this.user = userData.user;
    this.token = userData.token;
    localStorage.setItem('user', JSON.stringify(userData.user));
    localStorage.setItem('authToken', userData.token);
  }

  getUser() {
    return this.user;
  }

  isSeller(): boolean {
    return this.user?.role === 'seller';
  }

  isAdmin(): boolean {
    return this.user?.role === 'admin';
  }

  getToken() {
    return this.token;
  }

  isLoggedIn(): boolean {
    return !!this.user;
  }

  logout() {
    this.user = null;
    this.token = null;
    localStorage.removeItem('user');
    localStorage.removeItem('authToken');
  }
}
