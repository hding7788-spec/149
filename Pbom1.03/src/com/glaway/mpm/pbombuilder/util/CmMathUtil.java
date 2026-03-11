package com.glaway.mpm.pbombuilder.util;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

/**
 * <br>
 * Created on 2012-11-21
 * 
 * @author chenyunlong
 */
public class CmMathUtil {

   static double              EPSILON                = 0.00001;
   private static double      TOL                    = 0.000001;

   private static Matrix3f    identityMatrix         = new Matrix3f();

   public static final int    TRANSLATIONSCALE3DXML  = 1000;
   public static final String IDENTITYMATRIXSTR      = "1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1";
   public static final String IDENTITYMATRIX3DXMLSTR = "1 0 0 0 1 0 0 0 1 0 0 0";

   static {
      identityMatrix.setIdentity();

   }

   // -----------------------------------------------
   /**
    * 
    * vectorsToMatrice4: generate a Matrix4d from two vectors 3d defining a
    * rotation and a translation
    * 
    * @param angles -
    *        Vector3d rotation
    * @param position -
    *        Vector3d translation
    * @return matrice - Matrix3d de rotation correspondante
    */
   // ------------------------------------------------
   public static Matrix4d vectorsToMatrice4(Vector3d angles, Vector3d position) {
      Matrix4d m1 = new Matrix4d();
      Matrix4d m2 = new Matrix4d();
      m1.rotZ(angles.z);
      m2.rotY(angles.y);
      m1.mul(m2);
      m2.rotX(angles.x);
      m1.mul(m2);
      m1.setTranslation(position);
      return m1;
   }

   // -----------------------------------------------
   /**
    * 
    * vectorsToMatrice4VDI: generates a Matrix4d from two Vector3d defining a
    * rotation and a translation.
    * 
    * The order of rotation compositions originated in aeronautical convention
    * of composing rotations in the order Roll, Pitch then Yaw.
    * 
    * @param angles -
    *        Vector3d rotation
    * @param position -
    *        Vector3d translation
    * @return Matrix4d - resulting corresponding Matrix4f
    */
   // -----------------------------------------------
   public static Matrix4d vectorsToMatrice4VDI(final Vector3d angles, final Vector3d position) {
      Matrix4d m1 = new Matrix4d();
      Matrix4d m2 = new Matrix4d();
      m1.rotY(angles.y);
      m2.rotX(angles.x);
      m1.mul(m2);
      m2.rotZ(angles.z);
      m1.mul(m2);
      m1.setTranslation(position);
      return m1;
   }

   // -----------------------------------------------
   /**
    * 
    * matrice4ToTrans: generate a Vector3d translation from a Matrix4d
    * 
    * @param mat -
    *        Matrix4d
    * @return Vector3d
    */
   // ------------------------------------------------
   public static Vector3d matrice4ToTrans(Matrix4d mat) {
      Vector3d vect = new Vector3d();
      mat.get(vect);
      return vect;
   }

   // -----------------------------------------------
   /**
    * 
    * matrice4ToAngles: generate a Vector3d rotation from a Matrix4d.
    * 
    * @param mat -
    *        Matrix4d
    * @return Vector3d
    */
   // ------------------------------------------------
   public static Vector3d matrice4ToAngles(Matrix4d mat) {
      Vector3d angles = new Vector3d();
      angles.x = Math.atan2(mat.m21, mat.m22);
      if (Math.abs(angles.x) < EPSILON)
         angles.x = 0.0d;
      angles.y = -Math.asin(mat.m20);
      if (Math.abs(angles.y) < EPSILON)
         angles.y = 0.0d;
      angles.z = Math.atan2(mat.m10, mat.m00);
      if (Math.abs(angles.z) < EPSILON)
         angles.z = 0.0d;
      return angles;
   }

   // -----------------------------------------------
   /**
    * 
    * combineMatrix4: multiply two Matrix4d
    * 
    * @param fatherMat -
    *        Matrix4d global axis ( father )
    * @param relmat -
    *        Matrix4d relative axis
    * @return Matrix4d
    */
   // ------------------------------------------------
   public static Matrix4d combineMatrix4(Matrix4d fatherMat, Matrix4d relMat) {
      Matrix4d combmat = new Matrix4d();
      combmat.mul(fatherMat, relMat);
      return combmat;
   }

   // -----------------------------------------------
   /**
    * 
    * anglesToMatrice: creation de la matrice de rotation 3*3 a partir des
    * angles en X, Y et Z passes en radians.
    * 
    * @param angles -
    *        matrice(1,3) definissant les angles en X, Y et Z ( en radians )
    * @return matrice - Matrix3d de rotation correspondante
    */
   // ------------------------------------------------
   public static Matrix3d anglesToMatrice(Vector3d angles) {
      Matrix3d matrice = new Matrix3d(1.0d, 0.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 0.0d, 1.0d);
      if ((Math.abs(angles.x) > EPSILON) || (Math.abs(angles.y) > EPSILON) || (Math.abs(angles.z) > EPSILON)) {
         double sinx = Math.sin(angles.x);
         double cosx = Math.cos(angles.x);
         double siny = Math.sin(angles.y);
         double cosy = Math.cos(angles.y);
         double sinz = Math.sin(angles.z);
         double cosz = Math.cos(angles.z);
         matrice.m00 = cosy * cosz;
         matrice.m01 = cosy * sinz;
         matrice.m02 = (-siny);
         matrice.m10 = (sinx * siny * cosz) - (cosx * sinz);
         matrice.m11 = (sinx * siny * sinz) + (cosx * cosz);
         matrice.m12 = sinx * cosy;
         matrice.m20 = (cosx * siny * cosz) + (sinx * sinz);
         matrice.m21 = (cosx * siny * sinz) - (sinx * cosz);
         matrice.m22 = cosx * cosy;
      }
      return matrice;
   }

   // -----------------------------------------------
   /**
    * 
    * matriceToAngles: calcul des angles en X, Y et Z a partir d'une matrice de
    * rotation 3*3.
    * 
    * @param matrice -
    *        rotation Matrix3d
    * @return angles - Vector3d that defines radian angles
    */
   // ------------------------------------------------
   public static Vector3d matriceToAngles(Matrix3d matrice) {
      double x, y, z;
      y = (-Math.asin(matrice.m02));
      if (Math.abs(y) < EPSILON)
         y = 0.0d;

      if ((Math.abs(matrice.m12) < EPSILON) && (Math.abs(matrice.m22) < EPSILON)) {
         x = 0.0d;
         if (matrice.m02 > 0.0d)
            z = Math.atan2(-matrice.m21, -matrice.m20);
         else
            z = Math.atan2(matrice.m21, matrice.m20);
         if (Math.abs(z) < EPSILON)
            z = 0.0d;
      } else {
         x = Math.atan2(matrice.m12, matrice.m22);
         if (Math.abs(x) < EPSILON)
            x = 0.0d;
         if ((Math.abs(matrice.m01) < EPSILON) && (Math.abs(matrice.m00) < EPSILON))
            z = 0.0d;
         else {
            z = Math.atan2(matrice.m01, matrice.m00);
            if (Math.abs(z) < EPSILON)
               z = 0.0d;
         }
      }
      Vector3d angles = new Vector3d(x, y, z);
      return angles;
   }

   // -----------------------------------------------
   /**
    * 
    * matriceToAnglesVDI: returns the 3 angles as a Vector3f from a Matrix3d
    * rotation To be used in in DVIse MockUp context for which the rotatiosn are
    * composed in the order Roll, Pitch than Yaw.
    * 
    * @param matrice -
    *        rotation Matrix3d
    * @return Vector3d - defines the three angles in radians
    */
   // ------------------------------------------------
   public static Vector3d matriceToAnglesVDI(final Matrix3d matrice) {
      double x, y, z;
      x = (-Math.asin(matrice.m21));
      if (Math.abs(x) < CmMathUtil.EPSILON) {
         x = 0.0d;
      }
      if ((Math.abs(matrice.m01) < CmMathUtil.EPSILON) && (Math.abs(matrice.m11) < CmMathUtil.EPSILON)) {
         z = 0.0d;
         y = (matrice.m21 > 0.0d) ? Math.atan2(-matrice.m10, -matrice.m12) : Math.atan2(matrice.m10, matrice.m12);
         if (Math.abs(x) < CmMathUtil.EPSILON) {
            y = 0.0d;
         }
      } else {
         z = Math.atan2(matrice.m01, matrice.m11);
         if (Math.abs(z) < CmMathUtil.EPSILON) {
            z = 0.0d;
         }
         if ((Math.abs(matrice.m20) < CmMathUtil.EPSILON) && (Math.abs(matrice.m22) < CmMathUtil.EPSILON)) {
            y = 0.0d;
         } else {
            y = Math.atan2(matrice.m20, matrice.m22);
            if (Math.abs(y) < CmMathUtil.EPSILON) {
               y = 0.0d;
            }
         }
      }
      Vector3d angles = new Vector3d(x, y, z);
      return angles;
   }

   // -----------------------------------------------
   /**
    * 
    * transformPoint: calcul de la position globale d'un point a partir de sa
    * position relative et de la position globale de son pere.
    * 
    * @param initialPos -
    *        Coordonnees relatives du point a transformer
    * @param matrice -
    *        matrice de rotation 3*3 relative
    * @param fatherPos -
    *        Coordonnees globale de l'origine du repere global
    * @return coords - Coordonnees du point transforme dans le repere global
    */
   // ------------------------------------------------
   public static Vector3d transformPoint(Vector3d initialPos, Matrix3d matrice, Vector3d fatherPos) {
      Vector3d coords = new Vector3d();
      coords.x = fatherPos.x + initialPos.x * matrice.m00 + initialPos.y * matrice.m10 + initialPos.z * matrice.m20;
      coords.y = fatherPos.y + initialPos.x * matrice.m01 + initialPos.y * matrice.m11 + initialPos.z * matrice.m21;
      coords.z = fatherPos.z + initialPos.x * matrice.m02 + initialPos.y * matrice.m12 + initialPos.z * matrice.m22;
      return coords;
   }

   // -----------------------------------------------
   /**
    * 
    * combineMatrix: service de combinaison de matrices 3*3
    * 
    * @param matriceRepere -
    *        matrice 3*3 du repere absolu
    * @param matriceRelative -
    *        matrice 3*3 du repere relatif
    * @return matrice - matrice 3*3 de rotation combinee
    */
   // ------------------------------------------------
   public static Matrix3d combineMatrix(Matrix3d matriceRepere, Matrix3d matriceRelative) {
      Matrix3d retmat = new Matrix3d();
      retmat.mul(matriceRepere, matriceRelative);
      return retmat;
   }

   // -----------------------------------------------
   /**
    * 
    * getNewCoords: compute the global coordinates of a point
    * 
    * @param matriceRepere -
    *        Matrix3d absolute father position
    * @param matriceRelative -
    *        Matrix3d relative child position
    * @param fatherPos -
    *        Position of the father
    * @param initialPos -
    *        Position of the child in relative to the father
    * @param newRot -
    *        IN/OUT Matrix3d new absolute child position
    * @param newPos -
    *        IN/OUT Vector3d new absolute child position
    */
   // ------------------------------------------------
   public static void getNewCoords(Matrix3d matriceRepere, Matrix3d matriceRelative, Vector3d fatherPos, Vector3d initialPos, Matrix3d newRot,
      Vector3d newPos) {
      newRot.mul(matriceRepere, matriceRelative);
      newPos.x = fatherPos.x + initialPos.x * matriceRepere.m00 + initialPos.y * matriceRepere.m10 + initialPos.z * matriceRepere.m20;
      newPos.y = fatherPos.y + initialPos.x * matriceRepere.m01 + initialPos.y * matriceRepere.m11 + initialPos.z * matriceRepere.m21;
      newPos.z = fatherPos.z + initialPos.x * matriceRepere.m02 + initialPos.y * matriceRepere.m12 + initialPos.z * matriceRepere.m22;
   }

   // -----------------------------------------------
   /**
    * 
    * matrix4ToMatrix3: convert a Matrix4d to a Matrix3d
    * 
    * @param matrix4d -
    *        Matrix4d
    * @return rotation - Matrix3d
    */
   // ------------------------------------------------
   public static Matrix3d matrix4ToMatrix3(Matrix4d matrix4d) {
      Vector3d angles = new Vector3d();
      if (matrix4d.determinant() > 0.0d) {
         angles.x = Math.atan2(matrix4d.m21, matrix4d.m22);
         angles.y = -Math.asin(matrix4d.m20);
         angles.z = Math.atan2(matrix4d.m10, matrix4d.m00);
      } else {
         Matrix3d matrix3d = new Matrix3d();
         matrix4d.getRotationScale(matrix3d);
         matrix3d.mul(-1D);
         angles.x = Math.atan2(matrix3d.m21, matrix3d.m22);
         angles.y = -Math.asin(matrix3d.m20);
         angles.z = Math.atan2(matrix3d.m10, matrix3d.m00);
      }
      Matrix3d rotation = anglesToMatrice(angles);
      return rotation;
   }

   /**
    * Compute distance. Compute the arithmetic distance between the vector
    * related to the matrix A and the vector to the matrix B.
    * 
    * @param A
    *        the matrix A
    * @param B
    *        the matrix B
    * 
    * @return the double
    */
   public static double computeDistance(Matrix4d m1, Matrix4d m2) {
      if ((m1 != null) && (m2 != null)) {
         Vector3d v1 = matrice4ToTrans(m1);
         Vector3d v2 = matrice4ToTrans(m2);
         return Math.sqrt(Math.pow(v1.x - v2.x, 2.0d) + Math.pow(v1.y - v2.y, 2.0d) + Math.pow(v1.z - v2.z, 2.0d));
      } else
         return -1;
   }

   public static double[] getTranslationFromMatrix4d(Matrix4d mat) {
      Vector3d v = new Vector3d();
      mat.get(v);
      if (Math.abs(v.x) > TOL || Math.abs(v.y) > TOL || Math.abs(v.z) > TOL) {
         double[] d = new double[3];
         d[0] = v.x;
         d[1] = v.y;
         d[2] = v.z;
         return d;
      }

      return null; // return null if not non-zero value
   }

   public static float[] getOrientationFromMatrix4d(Matrix4d mat) {
      Matrix3f rot = new Matrix3f();
      mat.getRotationScale(rot);
      if (!rot.epsilonEquals(identityMatrix, (float) TOL)) {
         float[] f = new float[9];
         f[0] = rot.m00;
         f[1] = rot.m10;
         f[2] = rot.m20;
         f[3] = rot.m01;
         f[4] = rot.m11;
         f[5] = rot.m21;
         f[6] = rot.m02;
         f[7] = rot.m12;
         f[8] = rot.m22;
         return f;
      }

      return null; // retun null if not non-identity
   }

   public static String getLocationAsString(Matrix4d m4d) {

      String occurrenceLocation =
         m4d.m00 + " " + m4d.m01 + " " + m4d.m02 + " " + m4d.m03 + " " + m4d.m10 + " " + m4d.m11 + " " + m4d.m12 + " " + m4d.m13 + " " + m4d.m20
            + " " + m4d.m21 + " " + m4d.m22 + " " + m4d.m23 + " " + m4d.m30 + " " + m4d.m31 + " " + m4d.m32 + " " + m4d.m33;
      return occurrenceLocation;
   }

   public static Matrix4d getMatrix4dFromString(String matrix4dStr) {
      Matrix4d ret = new Matrix4d();
      ret.setIdentity();
      
      if (matrix4dStr != null) {
         double[] d = new double[16];

         String[] values = matrix4dStr.split("[ ,��]+");
         if (values.length >= 16) {
            for (int i = 0; i < 16; i++) {
               try {
                  d[i] = Double.parseDouble(values[i]);
               } catch (NumberFormatException e) {
                  // do nothing
               }
            }
            
            ret.set(d);
         }
      }
      
      return ret;
   }

   public static String get3dXmlLocation(Matrix4d m4d) {

      Matrix3d rotation = new Matrix3d();
      Vector3d translation = new Vector3d();

      if (m4d == null) {
         m4d = new Matrix4d();
         m4d.setIdentity();
      }
      m4d.getRotationScale(rotation);
      m4d.get(translation);

      String occurrenceLocation =
         rotation.m00 + " " + rotation.m10 + " " + rotation.m20 + " " + rotation.m01 + " " + rotation.m11 + " " + rotation.m21 + " " + rotation.m02
            + " " + rotation.m12 + " " + rotation.m22 + " " + translation.x * TRANSLATIONSCALE3DXML + " " + translation.y * TRANSLATIONSCALE3DXML
            + " " + translation.z * TRANSLATIONSCALE3DXML;

      if (occurrenceLocation.equals("0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0")) {
         occurrenceLocation = "1.0 0.0 0.0 0.0 1.0 0.0 0.0 0.0 1.0 0.0 0.0 0.0";
      }
      return occurrenceLocation;
   }
}
