import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { initializeApp } from '@angular/fire/app';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-manage-media',
  templateUrl: './manage-media.component.html',
  styleUrls: ['./manage-media.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ]
})
export class ManageMediaComponent {
  selectedFile: File | null = null;
  productId: string = '';
  images: string[] = []; // Array to hold image URLs
  uploadError: boolean = false;
  previewImage: boolean = false;
  constructor(private route: ActivatedRoute) {
    if (this.route.snapshot.paramMap.has('id')) {
      this.productId = this.route.snapshot.paramMap.get('id')!;
      // Fetch existing images for the product
    }
  }

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  addImage() {
    if (!this.selectedFile) {
      this.uploadError = true;
      console.error("No file selected");
      return;
    }
    const formData = new FormData();
    formData.append('image', this.selectedFile);   // file
    formData.append('productId', this.productId);  // string or number
  }

  deleteFile(index: number) {
    this.images.splice(index, 1);
  }
}
