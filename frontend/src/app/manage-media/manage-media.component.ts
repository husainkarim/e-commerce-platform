import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// Import necessary Firebase Storage functions
import {
  Storage,
  ref,             // Used to create a reference to a file or folder
  uploadBytes,
  getDownloadURL,
  deleteObject,
  // refFromURL is NOT available here, so we remove it from imports
} from '@angular/fire/storage';

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
export class ManageMediaComponent implements OnInit {
  images: string[] = [];
  selectedFile: File | null = null;
  previewImage: string | null = null;
  uploadError: string | null = null;

  constructor(private storage: Storage) { }

  ngOnInit(): void {
    // Implement loading existing images from your database or listing from storage
    // Example: this.loadProductImages();
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.previewImage = URL.createObjectURL(this.selectedFile);
      this.uploadError = null;
    } else {
      this.selectedFile = null;
      this.previewImage = null;
    }
  }

  async addImage(): Promise<void> {
    if (!this.selectedFile) {
      this.uploadError = 'Please select an image file to upload.';
      return;
    }

    try {
      this.uploadError = null;

      const filePath = `product_images/${Date.now()}_${this.selectedFile.name}`;
      const imageRef = ref(this.storage, filePath); // Create a reference to the storage location

      await uploadBytes(imageRef, this.selectedFile);
      console.log('File uploaded to storage.');

      const downloadUrl = await getDownloadURL(imageRef);
      console.log('Download URL obtained:', downloadUrl);

      this.images.push(downloadUrl);
      this.selectedFile = null;
      this.previewImage = null;

      const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
      if (fileInput) {
          fileInput.value = '';
      }

      console.log('Image successfully added and displayed.');

    } catch (error: any) {
      this.uploadError = `Failed to upload image: ${error.message || 'Unknown error'}`;
      console.error('Image upload error:', error);
    }
  }

  /**
   * Helper function to extract the storage path from a Firebase Storage download URL.
   * A typical Firebase Storage URL looks like:
   * https://firebasestorage.googleapis.com/v0/b/<bucket>/o/<path/to/file>?alt=media&token=...
   * We need the <path/to/file> part, which is URL-encoded.
   * @param downloadUrl The full download URL of the file.
   * @returns The decoded full path of the file in the storage bucket.
   */
  private getPathFromDownloadUrl(downloadUrl: string): string | null {
    try {
      const parts = downloadUrl.split('/o/');
      if (parts.length > 1) {
        let pathWithQuery = parts[1];
        const queryIndex = pathWithQuery.indexOf('?');
        if (queryIndex !== -1) {
          pathWithQuery = pathWithQuery.substring(0, queryIndex);
        }
        return decodeURIComponent(pathWithQuery);
      }
    } catch (e) {
      console.error("Error parsing download URL:", e);
    }
    return null;
  }

  async deleteFile(index: number): Promise<void> {
    const imageUrlToDelete = this.images[index];
    if (!imageUrlToDelete) {
      console.warn('No image URL found at this index to delete.');
      return;
    }

    if (!confirm('Are you sure you want to delete this image?')) {
      return;
    }

    try {
      const filePath = this.getPathFromDownloadUrl(imageUrlToDelete);

      if (!filePath) {
        console.error('Could not extract file path from URL:', imageUrlToDelete);
        alert('Failed to delete: Invalid image URL.');
        return;
      }

      const imageRef = ref(this.storage, filePath); // Create a reference using the extracted path
      await deleteObject(imageRef);
      console.log('Image successfully deleted from Firebase Storage.');

      this.images.splice(index, 1);
      console.log('Image removed from local list.');

    } catch (error: any) {
      if (error.code === 'storage/object-not-found') {
        console.warn(`File not found in Storage: ${imageUrlToDelete}. Removing from local list anyway.`);
        this.images.splice(index, 1);
      } else {
        console.error('Error deleting image:', error);
        alert(`Failed to delete image: ${error.message || 'Unknown error'}`);
      }
    }
  }
}
