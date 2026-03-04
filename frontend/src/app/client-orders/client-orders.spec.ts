import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { ClientOrders } from './client-orders';
import { AuthServiceService } from '../auth-service.service';
import { ApiService } from '../api.service';

describe('ClientOrders', () => {
  let component: ClientOrders;
  let fixture: ComponentFixture<ClientOrders>;
  let apiService: jasmine.SpyObj<ApiService>;
  let authService: jasmine.SpyObj<AuthServiceService>;
  let router: Router;

  beforeEach(async () => {
    apiService = jasmine.createSpyObj<ApiService>('ApiService', ['getOrdersByUserId']);
    authService = jasmine.createSpyObj<AuthServiceService>('AuthServiceService', ['isLoggedIn', 'getUser']);

    authService.isLoggedIn.and.returnValue(true);
    authService.getUser.and.returnValue({ id: 'u1' });

    apiService.getOrdersByUserId.and.returnValue(of({
      orders: [
        { id: 'o1', createdAt: '2025-01-01T10:00:00Z', status: 'PENDING', items: [{ quantity: 2 }] },
        { id: 'o2', createdAt: '2025-01-02T10:00:00Z', status: 'DELIVERED', items: [{ quantity: 1 }] }
      ]
    }));

    await TestBed.configureTestingModule({
      imports: [ClientOrders, RouterTestingModule],
      providers: [
        { provide: ApiService, useValue: apiService },
        { provide: AuthServiceService, useValue: authService }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    fixture = TestBed.createComponent(ClientOrders);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and load orders', () => {
    expect(component.orders.length).toBe(2);
    expect(component.filteredOrders.length).toBe(2);
  });

  it('should filter by status and date', () => {
    component.searchStatus = 'DELIVERED';
    component.searchDate = '2025-01-02';
    component.applyFilters();

    expect(component.filteredOrders.length).toBe(1);
    expect(component.filteredOrders[0].id).toBe('o2');
  });

  it('should clear filters', () => {
    component.searchStatus = 'PENDING';
    component.searchDate = '2025-01-01';
    component.clearFilters();

    expect(component.searchStatus).toBe('All');
    expect(component.searchDate).toBe('');
  });

  it('should calculate total items', () => {
    const total = component.calculateTotalItems({ items: [{ quantity: 2 }, { quantity: 3 }] } as any);
    expect(total).toBe(5);
  });

  it('should navigate to order details', () => {
    component.viewOrderDetails('o1');
    expect(router.navigate).toHaveBeenCalledWith(['/orders', 'o1']);
  });

  it('should redirect to login when not logged in', async () => {
    await TestBed.resetTestingModule();
    apiService = jasmine.createSpyObj<ApiService>('ApiService', ['getOrdersByUserId']);
    authService = jasmine.createSpyObj<AuthServiceService>('AuthServiceService', ['isLoggedIn', 'getUser']);
    authService.isLoggedIn.and.returnValue(false);

    await TestBed.configureTestingModule({
      imports: [ClientOrders, RouterTestingModule],
      providers: [
        { provide: ApiService, useValue: apiService },
        { provide: AuthServiceService, useValue: authService }
      ]
    }).compileComponents();

    const localRouter = TestBed.inject(Router);
    spyOn(localRouter, 'navigate');

    fixture = TestBed.createComponent(ClientOrders);
    fixture.detectChanges();

    expect(localRouter.navigate).toHaveBeenCalledWith(['/login']);
  });
});
