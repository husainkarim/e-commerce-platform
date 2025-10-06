import { Routes } from '@angular/router';
import { ProductsComponent } from './products/products.component';
import { ProductDetailComponent } from './product-detail/product-detail.component';
import { LoginComponent } from './login/login.component';
import { SignupComponent } from './signup/signup.component';
import { HomeComponent } from './home/home.component'
import { ProfileComponent } from './profile/profile.component';
import { SellerDashboardComponent } from './seller-dashboard/seller-dashboard.component';
import { ProductFormComponent } from './product-form/product-form.component';
import { ManageMediaComponent } from './manage-media/manage-media.component';
import { NotFoundComponent } from './not-found/not-found.component';


export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'products', component: ProductsComponent },
  { path: 'products/:id', component: ProductDetailComponent },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'seller/dashboard', component: SellerDashboardComponent },
  { path: 'seller/dashboard/create-product', component: ProductFormComponent },
  { path: 'seller/dashboard/edit-product/:id', component: ProductFormComponent },
  { path: 'seller/dashboard/media/:id', component: ManageMediaComponent },
  { path: '**', component: NotFoundComponent }
];
