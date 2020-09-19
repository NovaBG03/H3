import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormControl, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-tree-new',
  templateUrl: './tree-new.component.html',
  styleUrls: ['./tree-new.component.css']
})
export class TreeNewComponent implements OnInit {
  treeForm: FormGroup;

  constructor(private route: ActivatedRoute, private router: Router) {
  }

  ngOnInit(): void {
    this.initForm();
  }

  onCancel(): void {
    this.router.navigate(['..'], {relativeTo: this.route});
  }

  createTree(): void {
    console.log(this.treeForm.value);
  }

  private initForm(): void {
    this.treeForm = new FormGroup({
      name: new FormControl(null, [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(225)
      ]),
      isPrivate: new FormControl()
    });
  }
}
