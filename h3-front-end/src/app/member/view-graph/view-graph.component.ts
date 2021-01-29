import {Component, OnDestroy, OnInit} from '@angular/core';
import {Couple, Graph, FamilyMember} from '../../shared/dtos.model';
import {Subscription} from 'rxjs';
import {MemberService} from '../member.service';
import {ActivatedRoute} from '@angular/router';
import {TreeService} from '../../tree/tree.service';
import {AuthService} from '../../authentication/auth.service';
import {map, switchMap, tap} from 'rxjs/operators';
import * as d3 from 'd3';

@Component({
  selector: 'app-view-graph',
  templateUrl: './view-graph.component.html',
  styleUrls: ['./view-graph.component.css']
})
export class ViewGraphComponent implements OnInit, OnDestroy {
  treeId: number;
  couples: Couple[];
  editingMember: FamilyMember = null;
  isCreatingMember = false;
  isOwner: boolean;
  private userSub: Subscription;

  // new
  data: Graph =
    {
      nodes: [],
      links: []
    };

  private chartHeight = 600;
  private chartWidth = 800;

  constructor(private memberService: MemberService,
              private route: ActivatedRoute,
              private treeService: TreeService,
              private authService: AuthService) {
  }

  ngOnInit(): void {
    // this.setUpTemplate();
    // this.createChart();

    this.userSub = this.route.parent.url.pipe(
      map(parentUrlSegment => +parentUrlSegment[0]),
      tap(treeId => this.treeId = treeId),
      switchMap(treeId => {
        return this.treeService.getTree(treeId).pipe(
          switchMap(tree => {
            return this.authService.user.pipe(
              map(user => user.username === tree.owner)
            );
          }));
      })
    ).subscribe(isOwner => {
      this.isOwner = isOwner;
      this.loadMembers();
    });
  }

  onStartCreating(): void {
    this.isCreatingMember = true;
  }

  onFinishEditing(isChanged: boolean): void {
    this.editingMember = null;
    this.isCreatingMember = false;
    if (isChanged) {
      this.loadMembers();
    }
  }

  ngOnDestroy(): void {
    this.userSub.unsubscribe();
  }

  // private remove(): void {
  //   this.chart.on('click', (sender, node) => {
  //     const currentNode = node.node;
  //     this.editingMember = this.familyMembers.getMember(currentNode.id);
  //   });
  // }

  private loadMembers(): void {
    console.log(this.treeId);
    this.memberService.getCouples(this.treeId).subscribe(couples => {
      // this.couples = couples;
      this.data.nodes = couples;
      if (this.data.nodes.length > 0) {
        this.createGraph();
        // this.displayData();
      }
    });
  }

  private createGraph(): void {
    const simulation = d3.forceSimulation(this.data.nodes)
      .force('link', d3.forceLink(this.data.links)
        .distance(0)
        .strength(1))
      .force('charge', d3.forceManyBody()
        .strength(-10000))
      .force('x', d3.forceX())
      .force('y', d3.forceY());

    // Create SVG
    const svg = d3.select('#graph')
      .append('svg')
      .attr('height', this.chartHeight)
      .attr('width', this.chartWidth)
      .append('g')
      .attr('transform', `translate(${this.chartWidth / 2}, ${this.chartHeight / 2})`);


    // Extract Links and create link force
    this.extractLinks();
    simulation.force('link', d3.forceLink(this.data.links));

    // Create Links
    const links = svg.selectAll('lines')
      .data(this.data.links)
      .enter()
      .append('line')
      .attr('stroke', '#999999')
      .attr('stroke-opacity', 0.6)
      .attr('stroke-width', 4);

    // Create Nodes
    const nodes = svg.selectAll('g.couple')
      .data(this.data.nodes)
      .enter()
      .append('g')
      .classed('couple', true);

    nodes.append('circle')
      .attr('fill', d => d.partnerParentId ? '#fff' : null)
      .attr('stroke', d => d.partnerParentId ? '#000' : null)
      .attr('stroke-width', 6)
      .attr('r', 30);

    const innerRadius = 30;
    const primaryParentNodes = nodes.filter(d => !!d.partnerParentId)
      .append('circle')
      .attr('fill', '#fff')
      .attr('stroke', '#000')
      .attr('stroke-width', 6)
      .attr('r', innerRadius)
      .attr('transform', `translate(${-innerRadius}, 0)`);

    const partnerParentNodes = nodes.filter(d => !!d.partnerParentId)
      .append('circle')
      .attr('fill', '#fff')
      .attr('stroke', '#000')
      .attr('stroke-width', 6)
      .attr('r', innerRadius)
      .attr('transform', `translate(${innerRadius}, 0)`);

    // titles
    nodes.filter(d => !d.partnerParentId)
      .append('title')
      .text(d => d.primaryParentName);

    primaryParentNodes.append('title')
      .text(d => d.primaryParentName);

    partnerParentNodes.append('title')
      .text(d => d.partnerParentName);

    // on tick
    simulation.on('tick', () => {
      // @ts-ignore
      links.attr('x1', d => d.source.x)  // @ts-ignore
        .attr('y1', d => d.source.y)  // @ts-ignore
        .attr('x2', d => d.target.x)  // @ts-ignore
        .attr('y2', d => d.target.y);

      nodes.attr('transform', d => `translate(${d.x}, ${d.y})`);
    });
  }

  private displayData(): void {

  }

  private extractLinks(): void {
    const startingDepth = 0;
    const firstNode = this.data.nodes
      .find(d => d.depthIndex === startingDepth);
    this.extractLinksForNode(firstNode);

    console.log(this.data.links);
  }

  private extractLinksForNode(node): void {
    const depthIndex = node.depthIndex;

    const children = this.data.nodes.filter(d =>
      d.depthIndex === depthIndex + 1
      && d.leftIndex > node.leftIndex
      && d.rightIndex < node.rightIndex);

    children.forEach(c => this.data.links.push({
      source: node,
      target: c
    }));

    children.forEach(c => this.extractLinksForNode(c));
  }
}
