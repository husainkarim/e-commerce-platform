import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthServiceService } from './auth-service.service';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = 'http://localhost:8080/api'; // need to be confirmed

  constructor(private http: HttpClient, private authServiceService: AuthServiceService) {}

  // User APIs
  signup(data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/users/signup`, data);
  }

  login(data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/users/login`, data);
  }

  profile(id: string): Observable<any> {
    console.log('Fetching profile for user ID:', id);
    return this.http.get(`${this.baseUrl}/users/profile?userId=${id}`, { headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${this.authServiceService.getToken()}` } });
  }

  // Product APIs


  // Media APIs
}
