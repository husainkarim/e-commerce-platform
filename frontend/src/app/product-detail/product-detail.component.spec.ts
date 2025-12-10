import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProductDetailComponent } from './product-detail.component';
import { ActivatedRoute } from '@angular/router'; // <--- NEW IMPORT

describe('ProductDetailComponent', () => {
  let component: ProductDetailComponent;
  let fixture: ComponentFixture<ProductDetailComponent>;

  // --- NEW: Define a mock for ActivatedRoute ---
  const mockActivatedRoute = {
    // Mocks snapshot access for reading initial parameters (e.g., product ID)
    snapshot: {
      paramMap: {
        get: (key: string) => '123' // Mock product ID
      }
    },
    // Mocks the observable params/queryParamMap, typically used in ngOnInit
    params: {
        subscribe: () => ({})
    },
    queryParamMap: {
        subscribe: () => ({})
    }
  };
  // ---------------------------------------------

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductDetailComponent],
      providers: [ // <--- NEW: Add the Providers array
        {
          provide: ActivatedRoute,
          useValue: mockActivatedRoute // Provide the mock implementation
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProductDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
