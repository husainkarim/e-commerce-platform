import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { ProductFormComponent } from './product-form.component';
import { ApiService } from '../api.service';
import { AuthServiceService } from '../auth-service.service';

describe('ProductFormComponent', () => {
  let component: ProductFormComponent;
  let fixture: ComponentFixture<ProductFormComponent>;
  let apiService: jasmine.SpyObj<ApiService>;
  let authService: jasmine.SpyObj<AuthServiceService>;
  let router: Router;

  const product = {
    id: 'p1',
    name: 'Phone',
    description: 'Smart',
    price: 100,
    quantity: 5,
    userId: 'u1',
    category: 'Electronics'
  };

  const createComponent = async (productId: string | null, loggedIn = true) => {
    apiService = jasmine.createSpyObj<ApiService>('ApiService', ['getProductById', 'createProduct', 'updateProduct']);
    authService = jasmine.createSpyObj<AuthServiceService>('AuthServiceService', ['isLoggedIn', 'getUser']);
    authService.isLoggedIn.and.returnValue(loggedIn);
    authService.getUser.and.returnValue({ id: 'u1' });

    apiService.getProductById.and.returnValue(of({ product }));
    apiService.createProduct.and.returnValue(of({}));
    apiService.updateProduct.and.returnValue(of({}));

    await TestBed.configureTestingModule({
      imports: [ProductFormComponent, RouterTestingModule],
      providers: [
        { provide: ApiService, useValue: apiService },
        { provide: AuthServiceService, useValue: authService },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap(productId ? { id: productId } : {}) } }
        }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    fixture = TestBed.createComponent(ProductFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  };

  beforeEach(() => {
    TestBed.resetTestingModule();
  });

  it('should create in create mode', async () => {
    await createComponent(null);
    expect(component).toBeTruthy();
    expect(component.isEditMode).toBeFalse();
  });

  it('should redirect to login when not logged in', async () => {
    await createComponent(null, false);
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should load product in edit mode', async () => {
    await createComponent('p1');
    expect(component.isEditMode).toBeTrue();
    expect(component.productForm.value.name).toBe('Phone');
  });

  it('should create product on submit', async () => {
    await createComponent(null);
    spyOn(window, 'alert');

    component.productForm.setValue({
      name: 'New',
      description: 'Desc',
      price: 50,
      quantity: 2,
      category: 'Electronics'
    });

    component.onSubmit();

    expect(apiService.createProduct).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/profile']);
    expect(window.alert).toHaveBeenCalled();
  });

  it('should update product on submit in edit mode', async () => {
    await createComponent('p1');
    spyOn(window, 'alert');

    component.product.id = 'p1';
    component.isEditMode = true;
    component.productForm.setValue({
      name: 'Updated',
      description: 'Desc',
      price: 60,
      quantity: 3,
      category: 'Electronics'
    });

    component.onSubmit();

    expect(apiService.updateProduct).toHaveBeenCalledWith('p1', jasmine.any(Object));
    expect(router.navigate).toHaveBeenCalledWith(['/profile']);
    expect(window.alert).toHaveBeenCalled();
  });
});
