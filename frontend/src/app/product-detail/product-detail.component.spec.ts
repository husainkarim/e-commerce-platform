import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { ProductDetailComponent } from './product-detail.component';
import { ApiService } from '../api.service';
import { AuthServiceService } from '../auth-service.service';

describe('ProductDetailComponent', () => {
  let component: ProductDetailComponent;
  let fixture: ComponentFixture<ProductDetailComponent>;
  let apiService: jasmine.SpyObj<ApiService>;
  let authService: jasmine.SpyObj<AuthServiceService>;
  let router: Router;

  const product = {
    id: 'p1',
    name: 'Phone',
    description: 'Smart',
    price: 100,
    originalPrice: 200,
    quantity: 5,
    userId: 'u1',
    category: 'Electronics'
  };

  beforeEach(async () => {
    apiService = jasmine.createSpyObj<ApiService>('ApiService', ['getProductById', 'getImagesByProductId', 'updateCart']);
    authService = jasmine.createSpyObj<AuthServiceService>('AuthServiceService', ['getUser', 'getToken']);
    authService.getUser.and.returnValue({ id: 'u1' });
    authService.getToken.and.returnValue('mock-token');

    apiService.getProductById.and.returnValue(of({ product }));
    apiService.getImagesByProductId.and.returnValue(of({ images: [{ imagePath: 'img1.png' }] }));
    apiService.updateCart.and.returnValue(of({ success: true }));

    await TestBed.configureTestingModule({
      imports: [ProductDetailComponent, RouterTestingModule],
      providers: [
        { provide: ApiService, useValue: apiService },
        { provide: AuthServiceService, useValue: authService },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: convertToParamMap({ id: 'p1' }) }
          }
        }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    fixture = TestBed.createComponent(ProductDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and load product data', () => {
    expect(component).toBeTruthy();
    expect(component.product.id).toBe('p1');
    expect(component.product.image).toBe('img1.png');
  });

  it('should update selected image', () => {
    component.selectImage('img2.png');
    expect(component.product.image).toBe('img2.png');
  });

  it('should calculate discount', () => {
    expect(component.calculateDiscount()).toBe(50);
  });

  it('should update quantity within bounds', () => {
    // Manually set a proper product object
    component.product = {
      ...product,
      images: ['img1.png'],
      image: 'img1.png',
      originalPrice: 200
    };
    fixture.detectChanges();

    component.selectedQuantity = 1;

    component.increaseQuantity();
    expect(component.selectedQuantity).toBe(2);

    component.decreaseQuantity();
    expect(component.selectedQuantity).toBe(1);

    component.updateQuantity('5');
    expect(component.selectedQuantity).toBe(5);

    component.updateQuantity('10');
    expect(component.selectedQuantity).toBe(5);

    component.updateQuantity('0');
    expect(component.selectedQuantity).toBe(1);
  });

  it('should return total price', () => {
    component.product = { ...product };
    component.selectedQuantity = 3;
    expect(component.getTotalPrice()).toBe(300); // 100 * 3
  });

  it('should add to cart and persist in localStorage', () => {
    spyOn(window, 'alert');
    localStorage.clear();
    apiService.updateCart.and.returnValue(of({ success: true }));

    component.product = { ...product };

    component.addToCart(2);

    expect(apiService.updateCart).toHaveBeenCalled();
    const saved = JSON.parse(localStorage.getItem('cartItems') || '[]');
    expect(saved.length).toBe(1);
    expect(saved[0].quantity).toBe(2);
    expect(saved[0].productId).toBe(product.id);
    expect(window.alert).toHaveBeenCalled();
  });

  it('should ignore addToCart when out of stock', () => {
    component.product.quantity = 0;
    component.addToCart(1);
    expect(apiService.updateCart).not.toHaveBeenCalled();
  });

  it('should navigate when no product id in route', async () => {
    await TestBed.resetTestingModule();
    apiService = jasmine.createSpyObj<ApiService>('ApiService', ['getProductById', 'getImagesByProductId', 'updateCart']);
    authService = jasmine.createSpyObj<AuthServiceService>('AuthServiceService', ['getUser']);
    authService.getUser.and.returnValue({ id: 'u1' });

    await TestBed.configureTestingModule({
      imports: [ProductDetailComponent, RouterTestingModule],
      providers: [
        { provide: ApiService, useValue: apiService },
        { provide: AuthServiceService, useValue: authService },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: convertToParamMap({}) } } }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    fixture = TestBed.createComponent(ProductDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    expect(router.navigate).toHaveBeenCalledWith(['/not-found']);
  });
});
