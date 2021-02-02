import {Component, HostListener, OnDestroy, OnInit} from '@angular/core';
import {Couple, FamilyMember, Gender, Graph} from '../../shared/dtos.model';
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
  familyMembers: FamilyMember[];

  coordinates: { x: number, y: number } = null;

  // new
  data: Graph =
    {
      nodes: [],
      links: []
    };
  private userSub: Subscription;
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

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    this.resetCoordinates();
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
    this.memberService.getCouples(this.treeId).subscribe(couples => {
      // this.couples = couples;
      this.data.nodes = couples;
      if (this.data.nodes.length > 0) {
        this.memberService.getFamilyMembers(this.treeId).subscribe(familyMembers => {
          this.familyMembers = familyMembers;
          this.createGraph();
          // this.displayData();
        });
      }
    });
  }

  private createGraph(): void {
    const image = 'https://cdn.balkan.app/shared/f1.png';
    const circleRadius = 40;
    const strokeWidth = 3;
    const imageRadius = circleRadius - strokeWidth;
    const childRatio = 0.8; // from 0 to 1
    const fontSize = 16;

    this.chartWidth = d3.select('#graph').node().getBoundingClientRect().width;

    const simulation = d3.forceSimulation(this.data.nodes)
      .velocityDecay(0.8)
      .alphaDecay(0.01)
      .force('link', d3.forceLink(this.data.links)
        // .distance(0)
        .strength(0.025))
      .force('charge', d3.forceManyBody()
        .strength(-200))
      .force('collide', d3.forceCollide()
        .radius(circleRadius * 2.5)
        .strength(1))
      .force('center', d3.forceCenter(this.chartWidth / 2, this.chartHeight / 2))
      .force('x', d3.forceX())
      .force('y', d3.forceY());

    // Create SVG
    const svg = d3.select('#graph')
      .append('svg')
      .attr('height', this.chartHeight)
      .attr('width', this.chartWidth)
      .on('contextmenu', e => e.preventDefault())
      .append('g');
    // .attr('transform', `translate(${this.chartWidth / 2}, ${this.chartHeight / 2})`);

    // Extract Links and create link force
    this.extractLinks();
    simulation.force('link', d3.forceLink(this.data.links));

    // Create Links
    svg.append('defs')
      .append('svg:marker')
      .attr('id', 'arrow')
      .attr('viewBox', '0 -5 10 10')
      .attr('refX', circleRadius / 16)
      .attr('refY', 0)
      .attr('markerWidth', circleRadius / 8)
      .attr('markerHeight', circleRadius / 8)
      .attr('orient', 'auto')
      .append('svg:path')
      .attr('d', 'M0,-5L10,0L0,5')
      .style('fill', '#999999');

    const links = svg.selectAll('polyline')
      .data(this.data.links)
      .enter()
      .append('polyline')
      .attr('stroke', '#999999')
      .attr('stroke-opacity', 0.6)
      .attr('stroke-width', 4)
      .attr('marker-mid', 'url(#arrow)');

    // Create Nodes
    const nodes = svg.selectAll('g.couple')
      .data(this.data.nodes)
      .enter()
      .append('g')
      .classed('couple', true);

    nodes.on('contextmenu', (e, d) => {
      this.coordinates = {x: e.clientX, y: e.clientY};
    });

    nodes.call(d3.drag()
      .on('start', (event, data) => {
        this.resetCoordinates();
      })
      .on('drag', (event, data) => {
        data.x = event.x;
        data.y = event.y;
        // d3.select(this).raise().attr('transform', d => `translate(${d.x},${d.y})`);
      }));

    const primaryParentCoupleNodes = nodes.filter(d => !!d.partnerParentId);
    const partnerParentCoupleNodes = nodes.filter(d => !!d.partnerParentId);
    const childNodes = nodes.filter(d => !d.partnerParentId);

    primaryParentCoupleNodes.append('text')
      .attr('text-anchor', 'end')
      .attr('font-size', `${fontSize}`)
      .attr('x', -circleRadius / 3)
      .attr('y', circleRadius + strokeWidth + fontSize)
      .text(d => d.primaryParentName);

    partnerParentCoupleNodes.append('text')
      .attr('text-anchor', 'start')
      .attr('font-size', `${fontSize}`)
      .attr('x', circleRadius / 3)
      .attr('y', circleRadius + strokeWidth + fontSize)
      .text(d => d.partnerParentName);

    childNodes.append('text')
      .attr('text-anchor', 'middle')
      .attr('font-size', `${fontSize}`)
      .attr('x', 0)
      .attr('y', circleRadius * childRatio + fontSize + 1)
      .text(d => d.primaryParentName);

    const parentsLinks = nodes.filter(d => !!d.partnerParentId);
    parentsLinks.append('circle')
      .attr('fill', '#fff')
      .attr('stroke', '#000')
      .attr('stroke-width', 2 * strokeWidth)
      .attr('r', circleRadius * childRatio);

    childNodes.append('circle')
      .attr('fill', '#fff')
      .attr('stroke', d => this.getStrokeColor(d.primaryParentId))
      .attr('stroke-width', strokeWidth)
      .attr('r', circleRadius * childRatio);

    // Create primary parents
    primaryParentCoupleNodes.append('circle')
      .attr('fill', '#fff')
      .attr('stroke', d => this.getStrokeColor(d.primaryParentId))
      .attr('stroke-width', strokeWidth)
      .attr('r', circleRadius)
      .attr('transform', `translate(${-circleRadius - (strokeWidth * 0.5)}, 0)`);

    // Create Partners
    partnerParentCoupleNodes.append('circle')
      .attr('fill', '#fff')
      .attr('stroke', d => this.getStrokeColor(d.partnerParentId))
      .attr('stroke-width', strokeWidth)
      .attr('r', circleRadius)
      .attr('transform', `translate(${circleRadius + (strokeWidth * 0.5)}, 0)`);

    // Images
    const primaryParentImage = primaryParentCoupleNodes.append('svg:image')
      .attr('x', -imageRadius - circleRadius - (strokeWidth * 0.5))
      .attr('y', -imageRadius)
      .attr('width', 2 * imageRadius)
      .attr('height', 2 * imageRadius)
      .attr('xlink:href', image)
      .style('cursor', 'pointer');

    const partnerParentImage = partnerParentCoupleNodes.append('svg:image')
      .attr('transform', `translate(${-imageRadius})`, 0)
      .attr('x', circleRadius + (strokeWidth * 0.5))
      .attr('y', -imageRadius)
      .attr('width', 2 * imageRadius)
      .attr('height', 2 * imageRadius)
      .attr('xlink:href', image)
      .style('cursor', 'pointer');

    const childImage = childNodes.append('svg:image')
      .attr('x', -imageRadius * childRatio)
      .attr('y', -imageRadius * childRatio)
      .attr('width', 2 * imageRadius * childRatio)
      .attr('height', 2 * imageRadius * childRatio)
      .attr('xlink:href', image)
      .style('cursor', 'pointer');

    // Events
    primaryParentImage
      .on('mouseover', this.handleImageMouseOver)
      .on('mouseout', this.handleImageMouseOut);
    // .on('contextmenu', (e, d) => this.displayMemberInfo(d.primaryParentId));

    partnerParentImage
      .on('mouseover', this.handleImageMouseOver)
      .on('mouseout', this.handleImageMouseOut);
    // .on('contextmenu', (e, d) => this.displayMemberInfo(d.partnerParentId));

    childImage
      .on('mouseover', this.handleImageMouseOver)
      .on('mouseout', this.handleImageMouseOut);
    // .on('contextmenu', (e, d) => this.displayMemberInfo(d.primaryParentId));


    // titles
    childImage.append('title')
      .text(d => d.primaryParentName);

    primaryParentImage.append('title')
      .text(d => d.primaryParentName);

    partnerParentImage.append('title')
      .text(d => d.partnerParentName);

    // on tick
    simulation.on('tick', () => {
      // @ts-ignore
      links.attr('x1', d => d.source.x)  // @ts-ignore
        .attr('y1', d => d.source.y)  // @ts-ignore
        .attr('x2', d => d.target.x)  // @ts-ignore
        .attr('y2', d => d.target.y);

      links.attr('points', d => `${d.source.x},${d.source.y} ${(d.source.x + d.target.x) / 2},${(d.source.y + d.target.y) / 2} ${d.target.x},${d.target.y}`);

      nodes.attr('transform', d => `translate(${d.x}, ${d.y})`);
    });
  }

  private handleImageMouseOver(event, data): void {
    d3.select(this)
      .attr('opacity', 0.8);
  }

  private handleImageMouseOut(event, data): void {
    d3.select(this)
      .attr('opacity', 1);
  }

  private displayMemberInfo(id: number): void {
    const member = this.familyMembers.find(f => f.id === id);
    this.editingMember = member;
  }

  private getStrokeColor(id: number): string {
    const member = this.familyMembers.find(f => f.id === id);

    if (!member) {
      return '#000';
    }

    switch (member.gender) {
      case Gender.FEMALE:
        return '#ff4500';
      case Gender.MALE:
        return '#039BE5';
      case Gender.UNKNOWN:
        return '#aeaeae';
    }
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

  private resetCoordinates(): void {
    if (this.coordinates) {
      this.coordinates = null;
    }
  }
}
