import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { Cart } from './cart';
import { AuthServiceService } from '../auth-service.service';
import { ApiService } from '../api.service';

describe('Cart', () => {
  let component: Cart;
  let fixture: ComponentFixture<Cart>;
  let apiService: jasmine.SpyObj<ApiService>;
  let authService: jasmine.SpyObj<AuthServiceService>;
  let router: Router;

  const productResponse = {
    product: {
      id: 'p1',
      name: 'Phone',
      description: 'Smart',
      price: 10,
      userId: 's1',
      category: 'Electronics'
    }
  };

  beforeEach(async () => {
    apiService = jasmine.createSpyObj<ApiService>('ApiService', [
      'getCartByUserId',
      'getProductById',
      'getImagesByProductId',
      'updateCart'
    ]);
    authService = jasmine.createSpyObj<AuthServiceService>('AuthServiceService', ['isLoggedIn', 'getUser']);

    authService.isLoggedIn.and.returnValue(true);
    authService.getUser.and.returnValue({ id: 'u1' });

    apiService.getCartByUserId.and.returnValue(of({
      cart: {
        items: [
          { sellerId: 's1', productId: 'p1', productName: 'Phone', category: 'Electronics', price: 10, quantity: 2 }
        ]
      }
    }));
    apiService.getProductById.and.returnValue(of(productResponse));
    apiService.getImagesByProductId.and.returnValue(of({ images: [{ imagePath: 'img.png' }] }));
    apiService.updateCart.and.returnValue(of({ success: true }));

    await TestBed.configureTestingModule({
      imports: [Cart, RouterTestingModule],
      providers: [
        { provide: ApiService, useValue: apiService },
        { provide: AuthServiceService, useValue: authService }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    fixture = TestBed.createComponent(Cart);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load cart items and compute totals', fakeAsync(() => {
    tick();
    expect(component.items.length).toBe(1);
    expect(component.subtotal).toBe(20);
    expect(component.total).toBe(20);
  }));

  it('should update quantity within bounds and persist cart', () => {
    component.items = [{
      id: 'p1',
      name: 'Phone',
      description: 'Smart',
      price: 10,
      quantity: 1,
      userId: 's1',
      image: 'img.png',
      category: 'Electronics'
    }];

    component.updateQuantity(component.items[0], 150);

    expect(component.items[0].quantity).toBe(99);
    expect(apiService.updateCart).toHaveBeenCalled();
  });

  it('should remove item and persist cart', () => {
    component.items = [
      { id: 'p1', name: 'Phone', description: '', price: 10, quantity: 1, userId: 's1', image: '', category: 'Electronics' },
      { id: 'p2', name: 'Case', description: '', price: 5, quantity: 1, userId: 's1', image: '', category: 'Electronics' }
    ];

    component.removeItem('p1');

    expect(component.items.length).toBe(1);
    expect(apiService.updateCart).toHaveBeenCalled();
  });

  it('should redirect to login when not logged in', async () => {
    await TestBed.resetTestingModule();
    authService = jasmine.createSpyObj<AuthServiceService>('AuthServiceService', ['isLoggedIn', 'getUser']);
    apiService = jasmine.createSpyObj<ApiService>('ApiService', ['getCartByUserId', 'getProductById', 'getImagesByProductId', 'updateCart']);
    authService.isLoggedIn.and.returnValue(false);

    await TestBed.configureTestingModule({
      imports: [Cart, RouterTestingModule],
      providers: [
        { provide: ApiService, useValue: apiService },
        { provide: AuthServiceService, useValue: authService }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    fixture = TestBed.createComponent(Cart);
    fixture.detectChanges();

    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });
});
