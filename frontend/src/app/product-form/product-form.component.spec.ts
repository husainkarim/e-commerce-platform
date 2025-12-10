import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProductFormComponent } from './product-form.component';
import { ActivatedRoute } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('ProductFormComponent', () => {
  let component: ProductFormComponent;
  let fixture: ComponentFixture<ProductFormComponent>;

  const mockActivatedRoute = {
    // This mocks the common scenario where a component reads a parameter from the route snapshot
    snapshot: {
      paramMap: {
        get: (key: string) => '123' // Always returns a mock ID
      }
    },
    // This mocks the common scenario where a component subscribes to route params
    params: {
        subscribe: () => ({})
    }
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductFormComponent, HttpClientTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: mockActivatedRoute
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProductFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
