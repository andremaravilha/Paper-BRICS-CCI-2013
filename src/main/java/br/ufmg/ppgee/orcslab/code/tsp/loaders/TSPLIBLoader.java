package br.ufmg.ppgee.orcslab.code.tsp.loaders;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.ufmg.ppgee.orcslab.code.datastructures.Matrix;
import br.ufmg.ppgee.orcslab.code.datastructures.SymmetricMatrix;
import jopt.core.FileLoader;
import jopt.core.Loader;
import jopt.exceptions.AttributeNotFoundException;

/**
 * This loader reads files with data from instances of the traveling salesman
 * problem (TSP), formatted in the standard of the TSPLIB (see
 * <a href="http://comopt.ifi.uni-heidelberg.de/software/TSPLIB95/">TSPLIB</a> for
 * more information).
 * 
 * <p> This loader provide the following attributes:
 * <table border="1" cellspacing="0" cellpadding="3">
 *   <tr>
 *     <th>Attribute</th>
 *     <th>Type</th>
 *     <th>Info</th>
 *   </tr>
 *   <tr>
 *     <td>cost-matrix</td>
 *     <td>{@code {@link Matrix}<{@link Double}>}</td>
 *     <td>The (i,j)-element of the cost matrix is the cost of visit the node j
 *         immediately after the node i. If does not exist a direct path from i to
 *         j, the (i,j)-element is equal to {@code null}.</td>
 *   </tr>
 * </table>
 * 
 * @author	Andre L. Maravilha
 * @version	2013.03.20
 *
 */
public class TSPLIBLoader extends FileLoader {

	@Override
	protected void doRead(Map<String, Object> data, Map<String, Object> parameters, Path... paths) throws IOException {

		Scanner scanner = new Scanner(paths[0].toFile());
		StringBuilder content = new StringBuilder();
		while (scanner.hasNextLine()) {
			content.append(scanner.nextLine());
			content.append('\n');
		}
		scanner.close();

		int dimension = readDimension(content);
		String edgeWeightType = readEdgeWeightType(content);
		Matrix<Double> costMatrix = null;

		switch (edgeWeightType) {
			case "ATT":
				costMatrix = loadATT(content, dimension);
				break;

			case "EUC_2D":
				costMatrix = loadEUC_2D(content, dimension);
				break;

			default:
				throw new IOException("This loader cannot read data of type " + edgeWeightType + ". Only ATT and EUC_2D are supported.");
		}

		data.put("cost-matrix", costMatrix);
	}

	// Auxiliary methods

	private Integer readDimension(CharSequence data) {
		Pattern pattern = Pattern.compile("DIMENSION[\\s]*:[\\s]*(?<dimension>\\d+)[\\s]*");
		Matcher matcher = pattern.matcher(data);
		matcher.find();
		return new Integer(matcher.group("dimension"));
	}

	private String readEdgeWeightType(CharSequence data) {
		Pattern pattern = Pattern.compile("EDGE_WEIGHT_TYPE[\\s]*:[\\s]*(?<type>[\\w]+)[\\s]*");
		Matcher matcher = pattern.matcher(data);
		matcher.find();
		return matcher.group("type");
	}

	private Matrix<Double> loadATT(CharSequence data, int dimension) {

		Matrix<Double> costMatrix = new SymmetricMatrix<>(dimension, null);
		double[][] nodes = new double[dimension][2];

		Pattern patternSection = Pattern.compile("NODE_COORD_SECTION[\\s]*(?<nodes>[\\w\\W]*)");
		Matcher matcherSection = patternSection.matcher(data);
		matcherSection.find();
		String nodesSection = matcherSection.group("nodes");

		Pattern pattern = Pattern.compile("\\d+[\\s]+([\\w\\W&&[^\\s]]+)[\\s]+([\\w\\W&&[^\\s]]+)[\\s]*");
		Matcher matcher = pattern.matcher(nodesSection);

		for (int i = 0; i < dimension; ++i) {
			matcher.find();
			double x = Double.parseDouble(matcher.group(1));
			double y = Double.parseDouble(matcher.group(2));
			nodes[i][0] = x;
			nodes[i][1] = y;
		}

		for (int i = 0; i < dimension; ++i) {
			for (int j = i + 1; j < dimension; ++j) {
				double xd = nodes[i][0] - nodes[j][0];
				double yd = nodes[i][1] - nodes[j][1];
				double weight = Math.ceil(Math.sqrt((xd * xd + yd * yd) / 10.0));
				costMatrix.set(weight, i, j);
			}
		}

		return costMatrix;
	}

	private Matrix<Double> loadEUC_2D(CharSequence data, int dimension) {

		Matrix<Double> costMatrix = new SymmetricMatrix<>(dimension, null);
		double[][] nodes = new double[dimension][2];

		Pattern patternSection = Pattern.compile("NODE_COORD_SECTION[\\s]*(?<nodes>[\\w\\W]*)");
		Matcher matcherSection = patternSection.matcher(data);
		matcherSection.find();
		String nodesSection = matcherSection.group("nodes");

		Pattern pattern = Pattern.compile("\\d+[\\s]+([\\w\\W&&[^\\s]]+)[\\s]+([\\w\\W&&[^\\s]]+)[\\s]*");
		Matcher matcher = pattern.matcher(nodesSection);

		for (int i = 0; i < dimension; ++i) {
			matcher.find();
			double x = Double.parseDouble(matcher.group(1));
			double y = Double.parseDouble(matcher.group(2));
			nodes[i][0] = x;
			nodes[i][1] = y;
		}

		for (int i = 0; i < dimension; ++i) {
			for (int j = i + 1; j < dimension; ++j) {
				double xd = nodes[i][0] - nodes[j][0];
				double yd = nodes[i][1] - nodes[j][1];
				double weight = Math.round(Math.sqrt(xd * xd + yd * yd));
				costMatrix.set(weight, i, j);
			}
		}

		return costMatrix;
	}
}
