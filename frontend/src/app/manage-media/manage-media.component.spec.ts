import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ManageMediaComponent } from './manage-media.component';
import { ActivatedRoute } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs'; // To create mock Observables
import { ApiService } from '../api.service'; // Import the service being mocked

describe('ManageMediaComponent', () => {
  let component: ManageMediaComponent;
  let fixture: ComponentFixture<ManageMediaComponent>;

  // 1. Mock ActivatedRoute to provide the 'id' param and a subscriber
  const mockActivatedRoute = {
    // Mimic the paramMap observable stream
    paramMap: of({
      get: (key: string) => (key === 'id' ? '123' : null),
    }),
  };

  // 2. FIX: Mock ApiService methods used by the component
  const mockApiService = {
    // 🐛 FIX 1: Mock the method called in loadProductImages()
    getImagesByProductId: (id: string) => of({ images: [] }),
    // 🐛 FIX 2: Mock the method called in addImage()
    addmedia: (formData: FormData) => of({ success: true }),
    // 🐛 FIX 3: Mock the method called in deleteFile()
    deleteImage: (data: any) => of({ success: true }),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageMediaComponent, HttpClientTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: mockActivatedRoute, // Use the correct mock
        },
        {
          // 🐛 FIX: Provide the complete mock for ApiService
          provide: ApiService,
          useValue: mockApiService,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ManageMediaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // Triggers ngOnInit and the faulty loadProductImages call
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
