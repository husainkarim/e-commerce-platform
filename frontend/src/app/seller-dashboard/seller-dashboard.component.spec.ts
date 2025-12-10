import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SellerDashboardComponent } from './seller-dashboard.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router'; // <--- NEW IMPORT
import { of } from 'rxjs';
import { AuthServiceService } from '../auth-service.service';

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

  const mockUserService = {
    // Assuming the component reads the user object synchronously
    currentUser: {
      id: 1,
      username: 'testseller'
    },
    // If the component uses an async method (like getCurrentUser().subscribe)
    getCurrentUser: () => of({ id: 1, username: 'testseller' }),
    // You might need to add mocks for other methods like login, logout, etc.
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        SellerDashboardComponent,
        HttpClientTestingModule
      ],
      providers: [
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        // 🐛 FIX: Provide the mock UserService
        { provide: AuthServiceService, useValue: mockUserService }
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
