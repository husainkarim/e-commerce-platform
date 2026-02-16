import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { Checkout } from './checkout';
import { AuthServiceService } from '../auth-service.service';
import { ApiService } from '../api.service';

describe('Checkout', () => {
  let component: Checkout;
  let fixture: ComponentFixture<Checkout>;
  let apiService: jasmine.SpyObj<ApiService>;
  let authService: jasmine.SpyObj<AuthServiceService>;
  let router: Router;

  beforeEach(async () => {
    apiService = jasmine.createSpyObj<ApiService>('ApiService', ['getProductById', 'getImagesByProductId', 'placeOrder']);
    authService = jasmine.createSpyObj<AuthServiceService>('AuthServiceService', ['isLoggedIn', 'getUser']);

    authService.isLoggedIn.and.returnValue(true);
    authService.getUser.and.returnValue({ id: 'u1', name: 'User', email: 'u@e.com' });

    apiService.getProductById.and.returnValue(of({ product: { id: 'p1', price: 10, imageUrl: '', quantity: 1 } }));
    apiService.getImagesByProductId.and.returnValue(of({ images: [{ imagePath: 'img.png' }] }));
    apiService.placeOrder.and.returnValue(of({ message: 'ok' }));

    await TestBed.configureTestingModule({
      imports: [Checkout, RouterTestingModule],
      providers: [
        { provide: ApiService, useValue: apiService },
        { provide: AuthServiceService, useValue: authService }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    localStorage.setItem('cartItems', JSON.stringify([
      { sellerId: 's1', productId: 'p1', productName: 'Phone', category: 'Electronics', price: 5, quantity: 2 }
    ]));

    fixture = TestBed.createComponent(Checkout);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should create and load cart items', fakeAsync(() => {
    tick();
    expect(component.items.length).toBe(1);
    expect(component.products.length).toBe(1);
    expect(component.subtotal).toBe(20);
  }));

  it('should clamp step navigation', () => {
    component.goTo(5);
    expect(component.step).toBe(2);
    component.goTo(-1);
    expect(component.step).toBe(0);
  });

  it('should not advance when address invalid', () => {
    component.shippingAddress.fullName = '';
    component.next();
    expect(component.step).toBe(0);
  });

  it('should confirm order and clear cart', () => {
    component.shippingAddress = {
      fullName: 'User',
      email: 'u@e.com',
      phone: '123',
      address: 'Street',
      city: 'City',
      country: 'Country',
      notes: ''
    };
    component.items = [
      { sellerId: 's1', productId: 'p1', productName: 'Phone', category: 'Electronics', price: 10, quantity: 1 }
    ];

    component.confirm();

    expect(component.confirmed).toBeTrue();
    expect(component.step).toBe(2);
    expect(apiService.placeOrder).toHaveBeenCalled();
    expect(localStorage.getItem('cartItems')).toBeNull();
  });

  it('should redirect to login when not logged in', async () => {
    await TestBed.resetTestingModule();
    apiService = jasmine.createSpyObj<ApiService>('ApiService', ['getProductById', 'getImagesByProductId', 'placeOrder']);
    authService = jasmine.createSpyObj<AuthServiceService>('AuthServiceService', ['isLoggedIn', 'getUser']);
    authService.isLoggedIn.and.returnValue(false);

    await TestBed.configureTestingModule({
      imports: [Checkout, RouterTestingModule],
      providers: [
        { provide: ApiService, useValue: apiService },
        { provide: AuthServiceService, useValue: authService }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    fixture = TestBed.createComponent(Checkout);
    fixture.detectChanges();

    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });
});
