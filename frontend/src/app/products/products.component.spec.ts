import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { ProductsComponent } from './products.component';
import { ApiService } from '../api.service';
import { AuthServiceService } from '../auth-service.service';

describe('ProductsComponent', () => {
  let component: ProductsComponent;
  let fixture: ComponentFixture<ProductsComponent>;
  let apiService: jasmine.SpyObj<ApiService>;
  let authService: jasmine.SpyObj<AuthServiceService>;

  beforeEach(async () => {
    apiService = jasmine.createSpyObj<ApiService>('ApiService', ['getProducts', 'getImagesByProductId']);
    authService = jasmine.createSpyObj<AuthServiceService>('AuthServiceService', ['isLoggedIn']);
    authService.isLoggedIn.and.returnValue(true);

    apiService.getProducts.and.returnValue(of({ products: [] }));
    apiService.getImagesByProductId.and.returnValue(of({ images: [] }));

    await TestBed.configureTestingModule({
      imports: [ProductsComponent],
      providers: [
        { provide: ApiService, useValue: apiService },
        { provide: AuthServiceService, useValue: authService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProductsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should filter by keyword, category, and price', () => {
    component.products = [
      { id: '1', name: 'Phone', description: 'Smart', price: 500, quantity: 1, userId: 'u', image: '', category: 'Electronics' },
      { id: '2', name: 'Book', description: 'Novel', price: 20, quantity: 1, userId: 'u', image: '', category: 'Books' }
    ];

    component.searchKeyword = 'phone';
    component.selectedCategory = 'Electronics';
    component.minPrice = 100;
    component.maxPrice = 600;

    component.applyFilters();

    expect(component.filteredProducts.length).toBe(1);
    expect(component.filteredProducts[0].id).toBe('1');
  });

  it('should sort products by price desc', () => {
    component.products = [
      { id: '1', name: 'A', description: '', price: 10, quantity: 1, userId: 'u', image: '' },
      { id: '2', name: 'B', description: '', price: 30, quantity: 1, userId: 'u', image: '' }
    ];

    component.sortOption = 'price-desc';
    component.applyFilters();

    expect(component.filteredProducts[0].price).toBe(30);
  });

  it('should calculate pagination and page numbers', () => {
    component.products = [];
    for (let i = 0; i < 30; i++) {
      component.products.push({ id: `${i}`, name: `P${i}`, description: '', price: i, quantity: 1, userId: 'u', image: '' });
    }

    component.itemsPerPage = 10;
    component.applyFilters();

    expect(component.totalPages).toBe(3);
    expect(component.getPageNumbers()).toEqual([1, 2, 3]);

    component.goToPage(2);
    expect(component.currentPage).toBe(2);
    expect(component.paginatedProducts.length).toBe(10);
  });

  it('should ignore invalid page navigation', () => {
    component.totalPages = 2;
    component.currentPage = 1;

    component.goToPage(0);
    expect(component.currentPage).toBe(1);

    component.goToPage(3);
    expect(component.currentPage).toBe(1);
  });

  it('should clear filters', () => {
    component.searchKeyword = 'x';
    component.selectedCategory = 'Books';
    component.minPrice = 5;
    component.maxPrice = 10;
    component.sortOption = 'price-asc';

    component.clearFilters();

    expect(component.searchKeyword).toBe('');
    expect(component.selectedCategory).toBe('');
    expect(component.minPrice).toBeNull();
    expect(component.maxPrice).toBeNull();
    expect(component.sortOption).toBe('');
  });
});
