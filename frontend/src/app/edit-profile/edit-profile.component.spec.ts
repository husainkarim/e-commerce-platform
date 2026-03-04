import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { EditProfileComponent } from './edit-profile.component';
import { AuthServiceService } from '../auth-service.service';
import { ApiService } from '../api.service';

describe('EditProfileComponent', () => {
  let component: EditProfileComponent;
  let fixture: ComponentFixture<EditProfileComponent>;
  let apiService: jasmine.SpyObj<ApiService>;
  let authService: jasmine.SpyObj<AuthServiceService>;

  const user = { id: 'u1', name: 'User', email: 'u@e.com', role: 'client', avatar: '1.png' };

  beforeEach(async () => {
    apiService = jasmine.createSpyObj<ApiService>('ApiService', ['updateProfile', 'updateRole']);
    authService = jasmine.createSpyObj<AuthServiceService>('AuthServiceService', ['getUser', 'setUser']);
    authService.getUser.and.returnValue({ ...user });

    apiService.updateProfile.and.returnValue(of({ success: true }));
    apiService.updateRole.and.returnValue(of({ success: true }));

    await TestBed.configureTestingModule({
      imports: [EditProfileComponent],
      providers: [
        { provide: ApiService, useValue: apiService },
        { provide: AuthServiceService, useValue: authService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EditProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not save when form is invalid', () => {
    component.profileForm.get('name')?.setValue('');
    component.onSave();
    expect(apiService.updateProfile).not.toHaveBeenCalled();
  });

  it('should save profile updates', () => {
    component.profileForm.get('name')?.setValue('New Name');
    component.profileForm.get('avatar')?.setValue('2.png');
    component.profileForm.get('password')?.setValue('newPass');

    component.onSave();

    expect(apiService.updateProfile).toHaveBeenCalledWith('u1', jasmine.any(Object));
    expect(authService.setUser).toHaveBeenCalled();
    expect(localStorage.getItem('user')).toContain('New Name');
  });

  it('should update role on role button', () => {
    component.onRoleButton();
    expect(apiService.updateRole).toHaveBeenCalled();
    expect(authService.setUser).toHaveBeenCalled();
  });
});
