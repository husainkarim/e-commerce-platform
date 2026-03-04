import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { of } from 'rxjs';
import { SellerOrders } from './seller-orders';
import { AuthServiceService } from '../auth-service.service';
import { ApiService } from '../api.service';

describe('SellerOrders', () => {
  let component: SellerOrders;
  let fixture: ComponentFixture<SellerOrders>;
  let apiService: jasmine.SpyObj<ApiService>;
  let authService: jasmine.SpyObj<AuthServiceService>;

  beforeEach(async () => {
    apiService = jasmine.createSpyObj<ApiService>('ApiService', ['getSellerOrders', 'updateOrderStatus']);
    authService = jasmine.createSpyObj<AuthServiceService>('AuthServiceService', ['getUser']);

    authService.getUser.and.returnValue({ id: 's1' });
    apiService.getSellerOrders.and.returnValue(of({
      orders: [
        {
          id: 'o1',
          status: 'PENDING',
          items: [{ quantity: 2 }],
          shippingAddress: { fullName: 'John Doe', email: 'john@example.com', address: '123 St', city: 'NYC', country: 'USA' },
          totalAmount: 100,
          createdAt: new Date().toISOString()
        },
        {
          id: 'o2',
          status: 'CONFIRMED',
          items: [{ quantity: 1 }],
          shippingAddress: { fullName: 'Jane Smith', email: 'jane@example.com', address: '456 Ave', city: 'LA', country: 'USA' },
          totalAmount: 50,
          createdAt: new Date().toISOString()
        }
      ]
    }));
    apiService.updateOrderStatus.and.returnValue(of({ success: true }));

    await TestBed.configureTestingModule({
      imports: [SellerOrders],
      providers: [
        { provide: ApiService, useValue: apiService },
        { provide: AuthServiceService, useValue: authService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SellerOrders);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and load orders', () => {
    expect(component.orders.length).toBe(2);
    expect(component.filteredOrders.length).toBe(2);
    expect(component.loading).toBeFalse();
  });

  it('should filter orders by status', () => {
    component.selectedStatus = 'PENDING';
    component.filterOrders();
    expect(component.filteredOrders.length).toBe(1);
  });

  it('should update status on filter change', () => {
    const event = { target: { value: 'CONFIRMED' } } as any;
    component.onStatusFilterChange(event);
    expect(component.selectedStatus).toBe('CONFIRMED');
  });

  it('should compute total quantity', () => {
    const total = component.getTotalQuantity([{ quantity: 2 }, { quantity: 3 }]);
    expect(total).toBe(5);
  });

  it('should update order status and filter', fakeAsync(() => {
    component.orders = [
      { id: 'o1', status: 'PENDING', items: [] } as any
    ];

    component.updateOrderStatus('o1', 'CONFIRMED');
    tick(300);

    expect(apiService.updateOrderStatus).toHaveBeenCalled();
    expect(component.orders[0].status).toBe('CONFIRMED');
  }));

  it('should return status classes and guards', () => {
    expect(component.getStatusClass('PENDING')).toBe('status-pending');
    expect(component.canAccept('PENDING')).toBeTrue();
    expect(component.canReject('PENDING')).toBeTrue();
    expect(component.canProcess('CONFIRMED')).toBeTrue();
    expect(component.canDeliver('PROCESSED')).toBeTrue();
    expect(component.canCancel('PROCESSED')).toBeTrue();
  });
});
