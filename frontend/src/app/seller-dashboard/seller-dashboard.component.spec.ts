import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SellerDashboardComponent } from './seller-dashboard.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router'; // Also import Router
import { of } from 'rxjs';
import { AuthServiceService } from '../auth-service.service';
import { ApiService } from '../api.service'; // Need to mock ApiService too!

describe('SellerDashboardComponent', () => {
  let component: SellerDashboardComponent;
  let fixture: ComponentFixture<SellerDashboardComponent>;

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
    // Mock for getUserProducts (Called in ngOnInit)
    getUserProducts: (userId: number) => of({ products: [{ id: 101, name: 'Test Product' }] }),
    // Mock for getImagesByProductId (Called inside the loop)
    getImagesByProductId: (productId: number) => of({ images: [{ imagePath: 'mock/path.jpg' }] }),
    // Mock for deleteProduct (Optional, but good practice)
    deleteProduct: (productId: string) => of({ success: true }),
  };


  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        SellerDashboardComponent,
        HttpClientTestingModule
      ],
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

    fixture = TestBed.createComponent(SellerDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and load products', () => {
    expect(component).toBeTruthy();
    // Optional: Assert that getUserProducts was called
    // expect(mockApiService.getUserProducts).toHaveBeenCalled();
  });
});
