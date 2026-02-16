import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { ClientDashboard } from './client-dashboard';
import { AuthServiceService } from '../auth-service.service';
import { ApiService } from '../api.service';

describe('ClientDashboard', () => {
  let component: ClientDashboard;
  let fixture: ComponentFixture<ClientDashboard>;
  let apiService: jasmine.SpyObj<ApiService>;
  let authService: jasmine.SpyObj<AuthServiceService>;

  const mockResponse = {
    clientOrders: {
      totalOrders: 2,
      totalSpent: 120,
      orders: [
        {
          id: 1,
          status: 'PENDING',
          createdAt: '2025-01-02T10:00:00Z',
          totalAmount: 80,
          items: [
            { productId: 'p1', productName: 'Phone', category: 'Electronics', quantity: 2 },
            { productId: 'p2', productName: 'Book', category: 'Books', quantity: 1 }
          ]
        },
        {
          id: 2,
          status: 'CANCELLED',
          createdAt: '2025-01-01T09:00:00Z',
          totalAmount: 40,
          items: [
            { productId: 'p3', productName: 'Toy', category: 'Toys', quantity: 3 }
          ]
        }
      ]
    }
  };

  beforeEach(async () => {
    apiService = jasmine.createSpyObj<ApiService>('ApiService', ['getClientData']);
    authService = jasmine.createSpyObj<AuthServiceService>('AuthServiceService', ['getUser']);

    authService.getUser.and.returnValue({ id: 'u1', name: 'User', email: 'u@e.com' });
    apiService.getClientData.and.returnValue(of(mockResponse));

    await TestBed.configureTestingModule({
      imports: [ClientDashboard],
      providers: [
        { provide: ApiService, useValue: apiService },
        { provide: AuthServiceService, useValue: authService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ClientDashboard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and compute analytics', () => {
    expect(component).toBeTruthy();
    expect(component.userName).toBe('User');
    expect(component.totalOrders).toBe(2);
    expect(component.totalSpent).toBe(120);
    expect(component.totalItemsPurchased).toBe(3);
    expect(component.mostBoughtProducts.length).toBe(2);
    expect(component.topCategories.length).toBe(2);
    expect(component.recentOrders.length).toBe(2);
  });

  it('should handle API errors', () => {
    apiService.getClientData.and.returnValue(throwError(() => new Error('fail')));
    component.generateAnalytics();
    expect(apiService.getClientData).toHaveBeenCalled();
  });
});
