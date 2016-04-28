package visualization;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeRow;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeData;
import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.HierarchicalDirectedGraph;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.statistics.spi.Statistics;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Lookup;

public class EdgeBetweenness implements Statistics, LongTask {

  public static final String EDGE_BETWEENNESS = "edgebetweenness";
  private double[] edgeBetweenness;
  private double edgeBetwNum;
  private boolean isCancelled;
  private boolean isDirected;
  private boolean isNormalized;
  private ProgressTicket progressTicket;
  private String report = "";
  /**
   * Nodes count
   */
  private int N;
  /**
   * Edges count
   */
  private int E;

  public EdgeBetweenness() {

    GraphController graphCont = Lookup.getDefault().lookup(GraphController.class);
    if (graphCont != null && graphCont.getModel() != null) {
      isDirected = graphCont.getModel().isDirected();
    }
  }

  @Override
  public void execute(org.gephi.graph.api.GraphModel gm, org.gephi.data.attributes.api.AttributeModel am) {
    HierarchicalGraph graph = null;

    if (isDirected) {
      graph = gm.getHierarchicalDirectedGraph();
    } else {
      graph = gm.getHierarchicalUndirectedGraph();
    }

    execute(graph, am);
  }

  public void execute(org.gephi.graph.api.HierarchicalGraph hGraph, org.gephi.data.attributes.api.AttributeModel am) {
    isCancelled = false;
    hGraph.readLock();

    N = hGraph.getNodeCount();
    E = hGraph.getEdgeCount();

    long startTime = System.currentTimeMillis();
    report += "Algorithm started \n";
    Progress.start(progressTicket, N);


    // Get column for EDGE_BETWEENNESS (create if needed)
    AttributeTable edgeTable = am.getEdgeTable();
    AttributeColumn edgeBetweennessCol = edgeTable.getColumn(EDGE_BETWEENNESS);

    if (edgeBetweennessCol != null) {
      edgeTable.removeColumn(edgeBetweennessCol);
    }
    edgeBetweennessCol = edgeTable.addColumn(EDGE_BETWEENNESS, "EdgeBetweenness", AttributeType.DOUBLE, AttributeOrigin.COMPUTED, new Double(0));


    // Allocate new array for betweenness of each edge
    // Inicialize to 1
    edgeBetweenness = new double[E];
    for (int i = 0; i < E; i++) {
      edgeBetweenness[i] = 1;
    }

    String l = "Creating list of nodes (time: " + (System.currentTimeMillis() - startTime) + ")\n";
    report += l;

    // Enumerate every node
    int nodeIndex = 0;
    int edgeIndex = 0;
    HashMap<Node, Integer> nodesIndex = new HashMap<Node, Integer>();
    HashMap<Edge, Integer> edgesIndex = new HashMap<Edge, Integer>();

    // Remember level in tree for every node
    for (Node n : hGraph.getNodes()) {
      nodesIndex.put(n, nodeIndex);
      nodeIndex++;
    }
    for (Edge e : hGraph.getEdges()) {
      edgesIndex.put(e, edgeIndex);
      edgeIndex++;
    }

    l = "List of nodes and indexes created (time: " + (System.currentTimeMillis() - startTime) + ")\n";
    report += l;


    l = "Creating queue for BFS (time: " + (System.currentTimeMillis() - startTime) + ")\n";
    report += l;

    int count = 0;
    for (Node rootNode : hGraph.getNodes()) {
      count++;
      Progress.progress(progressTicket, count);

      if (isCancelled) {
        hGraph.readUnlockAll();
        return;
      }

      Stack<Node> stack = new Stack<Node>();

      // Create new array for distance and inicialize to -1 values
      LinkedList<Node>[] predecessorList = new LinkedList[N];
      LinkedList<Edge>[] edgesList = new LinkedList[N];

      int[] distance = new int[N];
      for (int j = 0; j < N; j++) {
        predecessorList[j] = new LinkedList<Node>();
        edgesList[j] = new LinkedList<Edge>();
        distance[j] = -1;
      }

      int srcIndex = nodesIndex.get(rootNode);
      distance[srcIndex] = 0;

      // BFS
      LinkedList<Node> queue = new LinkedList<Node>();
      queue.addLast(rootNode);

      while (!queue.isEmpty()) {
        Node v = queue.removeFirst();
        stack.push(v);
        int vIndex = nodesIndex.get(v);

        for (Edge edge : getEdgeIteratorForNode(v, hGraph)) {
          Node neighNode = hGraph.getOpposite(v, edge);

          int neighIndex = nodesIndex.get(neighNode);

          // if 'neigh node' was found for the first time
          if (distance[neighIndex] < 0) {
            queue.addLast(neighNode);
            // distance from 'source node n' is '(distance to v) + 1'
            distance[neighIndex] = distance[vIndex] + 1;
          }

          // shortest path to 'neigh node' via 'v node'?
          if (distance[neighIndex] == (distance[vIndex] + 1)) {
            // Copy path from previous level
            predecessorList[neighIndex] = (LinkedList<Node>) ((predecessorList[vIndex]).clone());
            // Add new node into path
            predecessorList[neighIndex].addLast(v);

            // Copy path from previous level
            edgesList[neighIndex] = (LinkedList<Edge>) edgesList[vIndex].clone();
            // Add new edge into path
            edgesList[neighIndex].addLast(edge);

            // Increment betweenness value for each edge
            for (Edge e : edgesList[neighIndex]) {
              EdgeData edgeData = e.getEdgeData();
              if(edgeData!=null) {
                AttributeRow row = (AttributeRow) e.getEdgeData().getAttributes();
                Double val = (Double) row.getValue(edgeBetweennessCol);
                val += 1;
                row.setValue(edgeBetweennessCol, val);
              } /*else {
                logger.log(Level.INFO, "e.getEdgeData is null");
              }*/
            }
          }
        }
      } ///BFS
    }

    double sumVal = 0;
    double maxBetweenness = Double.NEGATIVE_INFINITY;

    if (isNormalized) {
      maxBetweenness = findMaxBetweenness(hGraph, edgeBetweennessCol);
      if (!isDirected) {
        maxBetweenness /= 2;
      }
    }

    // For undirected graph divide edge betweenness to half
    for (Edge e : hGraph.getEdgesAndMetaEdges()) {
      AttributeRow row = (AttributeRow) e.getEdgeData().getAttributes();
      Double val = (Double) row.getValue(edgeBetweennessCol);
      if (!isDirected) {
        val /= 2;
      }

      sumVal += val;

      if (isNormalized) {
        val /= maxBetweenness;
      }
      row.setValue(edgeBetweennessCol, val);
    }

    // sum of edge betweenness
    this.edgeBetwNum = sumVal;

    hGraph.readUnlockAll();
    Progress.finish(progressTicket);
    report += "Algorithm finished (time: " + (System.currentTimeMillis() - startTime) + ")\n";
  }

  @Override
  public boolean cancel() {
    return isCancelled = true;
  }

  @Override
  public void setProgressTicket(ProgressTicket pt) {
    this.progressTicket = pt;
  }

  @Override
  public String getReport() {
    return report;
  }

  public void setDirected(boolean isDirected) {
    this.isDirected = isDirected;
  }

  public boolean getDirected() {
    return isDirected;
  }

  public boolean isNormalized() {
    return isNormalized;
  }

  public void doNormalize(boolean isNormalized) {
    this.isNormalized = isNormalized;
  }

  double getEdgeBetweenness() {
    return edgeBetwNum;
  }

  private String printArr(LinkedList<Node> linkedList, int size) {
    if (size == 1) {
      return "[straight]";
    }

    String result = "[through " + linkedList.size() + " nodes: ";
    for (Node l : linkedList) {
      result += l.getId() + " (" + l.getNodeData().getLabel() + ")";
      result += ", ";
    }
    result += "]";
    return result;
  }

  private String printEdges(LinkedList<Edge> linkedList) {
    String result = "[through " + linkedList.size() + " edges: ";
    for (Edge e : linkedList) {
      result += " " + e.getId() + "(" + e.getSource().getNodeData().getLabel() + " to " + e.getTarget().getNodeData().getLabel() + "), ";
    }
    return result;
  }

  private EdgeIterable getEdgeIteratorForNode(Node node, HierarchicalGraph forGraph) {
    EdgeIterable edgeIter;
    if (isDirected) {
      edgeIter = ((HierarchicalDirectedGraph) forGraph).getOutEdgesAndMetaOutEdges(node);
    } else {
      edgeIter = forGraph.getEdgesAndMetaEdges(node);
    }

    return edgeIter;
  }

  private double findMaxBetweenness(HierarchicalGraph graph, AttributeColumn column) {
    double max = Double.NEGATIVE_INFINITY;

    for (Edge e : graph.getEdgesAndMetaEdges()) {
      final AttributeRow row = (AttributeRow) e.getEdgeData().getAttributes();
      final Double val = (Double) row.getValue(column);

      if (val > max) {
        max = val;
      }
    }
    
    return max;
  }
}
