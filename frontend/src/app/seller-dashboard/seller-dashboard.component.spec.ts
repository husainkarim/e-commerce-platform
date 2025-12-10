import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SellerDashboardComponent } from './seller-dashboard.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router'; // <--- NEW IMPORT

describe('SellerDashboardComponent', () => {
  let component: SellerDashboardComponent;
  let fixture: ComponentFixture<SellerDashboardComponent>;

  // Mock ActivatedRoute (essential for any component that reads from the route)
  const mockActivatedRoute = {
    snapshot: {
      paramMap: {
        get: (key: string) => '123'
      }
    },
    // Add other necessary mocks (like params, queryParams, etc.) if the component subscribes to them
    params: {
        subscribe: () => ({})
    }
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        SellerDashboardComponent,
        HttpClientTestingModule
      ],
      providers: [ // <--- ADDED PROVIDER ARRAY
        {
          provide: ActivatedRoute, // <--- FIX: Provide the mock route
          useValue: mockActivatedRoute
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SellerDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
