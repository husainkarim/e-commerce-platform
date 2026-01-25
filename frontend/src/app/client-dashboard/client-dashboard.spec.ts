import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientDashboard } from './client-dashboard';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router'; // Also import Router
import { of } from 'rxjs';
import { AuthServiceService } from '../auth-service.service';
import { ApiService } from '../api.service'; // Need to mock ApiService too!

describe('ClientDashboard', () => {
  let component: ClientDashboard;
  let fixture: ComponentFixture<ClientDashboard>;

  // 1. Mock Router (Since the component injects and uses it for navigation)
  const mockRouter = {
    navigate: jasmine.createSpy('navigate'),
    events: of(new NavigationEnd(1, '', '')) // Mock the router events observable
  };

  // 2. Mock ActivatedRoute
  const mockActivatedRoute = {
    // ... same as before
  };

  // 3. FIX: Mock AuthServiceService to match component usage
  const mockAuthService = {
    // 🐛 FIX 1: Provide the missing function that the component calls in the constructor
    isLoggedIn: () => true,

    // 🐛 FIX 2: Provide the missing function that the component calls in getUserProducts()
    getUser: () => ({ id: 1 }), // Must return an object with 'id'
  };

  // 4. FIX: Mock ApiService (Required for nested subscriptions)
  const mockApiService = {
    getClientData: (userId: string) => of({
      totalSpent: 1000,
      totalOrders: 5,
      totalItemsPurchased: 20,
      mostBoughtProducts: [],
      topCategories: [],
      recentOrders: []
    })
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientDashboard, HttpClientTestingModule],
      providers: [
        { provide: Router, useValue: mockRouter }, // Add Router mock
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        // Use the comprehensive mock for AuthServiceService
        { provide: AuthServiceService, useValue: mockAuthService },
        // Add ApiService mock to handle async calls
        { provide: ApiService, useValue: mockApiService }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClientDashboard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
