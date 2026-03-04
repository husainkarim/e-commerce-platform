import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { OrderDetail } from './order-detail';
import { ApiService } from '../api.service';
import { AuthServiceService } from '../auth-service.service';

describe('OrderDetail', () => {
  let component: OrderDetail;
  let fixture: ComponentFixture<OrderDetail>;
  let apiService: jasmine.SpyObj<ApiService>;
  let authService: jasmine.SpyObj<AuthServiceService>;
  let router: Router;

  beforeEach(async () => {
    apiService = jasmine.createSpyObj<ApiService>('ApiService', ['getOrderById', 'getImagesByProductId']);
    authService = jasmine.createSpyObj<AuthServiceService>('AuthServiceService', ['isLoggedIn', 'getUser']);

    authService.isLoggedIn.and.returnValue(true);
    authService.getUser.and.returnValue({ id: 'u1' });

    apiService.getOrderById.and.returnValue(of({
      order: {
        id: 'o1',
        userId: 'u1',
        sellerId: 's1',
        createdAt: '2025-01-01',
        updatedAt: '2025-01-02',
        status: 'PENDING',
        items: [
          { productId: 1, productName: 'Phone', quantity: 1, price: 10, image: '' }
        ],
        paymentMethod: 'CARD',
        totalAmount: 10,
        shippingAddress: { name: 'A', street: 'S', city: 'C', state: 'ST', zip: '000', phone: '1' },
        timeline: []
      },
      status: [{ status: 'PENDING', updatedAt: '2025-01-01', completed: true }]
    }));
    apiService.getImagesByProductId.and.returnValue(of({ images: [{ imagePath: 'img.png' }] }));

    await TestBed.configureTestingModule({
      imports: [OrderDetail, RouterTestingModule],
      providers: [
        { provide: ApiService, useValue: apiService },
        { provide: AuthServiceService, useValue: authService },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: convertToParamMap({ id: 'o1' }) } } }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    fixture = TestBed.createComponent(OrderDetail);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and load order details', () => {
    expect(component).toBeTruthy();
    expect(component.order?.id).toBe('o1');
    expect(component.order?.items[0].image).toBe('img.png');
    expect(component.loading).toBeFalse();
  });

  it('should navigate back', () => {
    component.goBack();
    expect(router.navigate).toHaveBeenCalledWith(['/orders']);
  });

  it('should redirect to login when not logged in', async () => {
    await TestBed.resetTestingModule();
    apiService = jasmine.createSpyObj<ApiService>('ApiService', ['getOrderById', 'getImagesByProductId']);
    authService = jasmine.createSpyObj<AuthServiceService>('AuthServiceService', ['isLoggedIn', 'getUser']);
    authService.isLoggedIn.and.returnValue(false);

    await TestBed.configureTestingModule({
      imports: [OrderDetail, RouterTestingModule],
      providers: [
        { provide: ApiService, useValue: apiService },
        { provide: AuthServiceService, useValue: authService },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: convertToParamMap({ id: 'o1' }) } } }
      ]
    }).compileComponents();

    const localRouter = TestBed.inject(Router);
    spyOn(localRouter, 'navigate');

    fixture = TestBed.createComponent(OrderDetail);
    fixture.detectChanges();

    expect(localRouter.navigate).toHaveBeenCalledWith(['/login']);
  });
});
