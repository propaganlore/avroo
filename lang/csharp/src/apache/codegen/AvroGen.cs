/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;

namespace Avro
{
    public class AvroGenTool
    {
        public static int Main(string[] args)
        {
            // Print usage if no arguments provided
            if (args.Length == 0)
            {
                Usage();
                return 1;
            }

            // Print usage if help requested
            if (args.Contains("-h") || args.Contains("--help"))
            {
                Usage();
                return 0;
            }

            if (args.Contains("--version") || args.Contains("-V"))
            {
                // Print version information
                // Note: Use InformationalVersion attribute
                // It is capable to include semver prerelease information label (if prerelease), e.g. 1.x.y-beta.z
                Console.WriteLine(typeof(AvroGenTool).Assembly.GetCustomAttribute<AssemblyInformationalVersionAttribute>().InformationalVersion);
                return 0;
            }

            // Parse command line arguments
            bool? isProtocol = null;
            string inputFile = null;
            string outputDir = null;
            List<string> msFiles = new List<string>();

            bool skipDirectoriesCreation = false;
            var namespaceMapping = new Dictionary<string, string>();
            for (int i = 0; i < args.Length; ++i)
            {
                if (args[i] == "-p")
                {
                    if (i + 1 >= args.Length)
                    {
                        Console.Error.WriteLine("Missing path to protocol file");
                        Usage();
                        return 1;
                    }

                    isProtocol = true;
                    inputFile = args[++i];
                }
                else if (args[i] == "-s")
                {
                    if (i + 1 >= args.Length)
                    {
                        Console.Error.WriteLine("Missing path to schema file");
                        Usage();
                        return 1;
                    }

                    isProtocol = false;
                    inputFile = args[++i];
                }
                else if (args[i] == "--namespace")
                {
                    if (i + 1 >= args.Length)
                    {
                        Console.Error.WriteLine("Missing namespace mapping");
                        Usage();
                        return 1;
                    }

                    var parts = args[++i].Split(new char[] { ':' }, StringSplitOptions.RemoveEmptyEntries);
                    if (parts.Length != 2)
                    {
                        Console.Error.WriteLine("Malformed namespace mapping. Required format is \"avro.namespace:csharp.namespace\"");
                        Usage();
                        return 1;
                    }

                    namespaceMapping[parts[0]] = parts[1];
                }
                else if (args[i] == "--skip-directories")
                {
                    skipDirectoriesCreation = true;
                }
                else if (args[i] == "-ms")
                {
                    msFiles.AddRange(args[++i].Split(new string[] { ",", ";" }, StringSplitOptions.RemoveEmptyEntries));
                    Console.WriteLine("Schema files to parse:");
                    foreach (var item in msFiles)
                    {
                        Console.WriteLine(item);
                    }
                }
                else if (outputDir == null)
                {
                    outputDir = args[i];
                    Console.WriteLine($"Files will generated at: {outputDir}");
                }
                else
                {
                    Console.Error.WriteLine("Unexpected command line argument: {0}", args[i]);
                    Usage();
                }
            }

            if (msFiles.Any())
                return GenSchema(msFiles, outputDir, namespaceMapping);


            // Ensure we got all the command line arguments we need
            bool isValid = true;
            int rc = 0;
            if (!isProtocol.HasValue || inputFile == null)
            {
                Console.Error.WriteLine("Must provide either '-p <protocolfile>' or '-s <schemafile>'");
                isValid = false;
            }
            else if (outputDir == null)
            {
                Console.Error.WriteLine("Must provide 'outputdir'");
                isValid = false;
            }


            if (!isValid)
            {
                Usage();
                rc = 1;
            }
            else if (isProtocol.Value)
                rc = GenProtocol(inputFile, outputDir, namespaceMapping);
            else
                rc = GenSchema(inputFile, outputDir, namespaceMapping, skipDirectoriesCreation);

            return rc;
        }

        static void Usage()
        {
            Console.WriteLine("{0}\n\n" +
                "Usage:\n" +
                "  avrogen -p <protocolfile> <outputdir> [--namespace <my.avro.ns:my.csharp.ns>]\n" +
                "  avrogen -s <schemafile> <outputdir> [--namespace <my.avro.ns:my.csharp.ns>]\n\n" +
                "  avrogen -ms <schemafiles> <outputdir> [--namespace <my.avro.ns:my.csharp.ns>]\n\n" +
                "Options:\n" +
                "  -h --help        Show this screen.\n" +
                "  -V --version     Show version.\n" +
                "  --namespace      Map an Avro schema/protocol namespace to a C# namespace.\n" +
                "                   The format is \"my.avro.namespace:my.csharp.namespace\".\n" +
                "                   May be specified multiple times to map multiple namespaces.\n"  +
                "  --skip-directories Skip creation of namespace directories. It will generate classes right inside output directory\n",
                AppDomain.CurrentDomain.FriendlyName);
        }

        public static int GenProtocol(string infile, string outdir,
            IEnumerable<KeyValuePair<string, string>> namespaceMapping)
        {
            try
            {
                string text = System.IO.File.ReadAllText(infile);

                CodeGen codegen = new CodeGen();
                codegen.AddProtocol(text, namespaceMapping);

                codegen.GenerateCode();
                codegen.WriteTypes(outdir);
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine("Exception occurred. " + ex.Message);
                return 1;
            }

            return 0;
        }

        public static int GenSchema(string infile, string outdir,
            IEnumerable<KeyValuePair<string, string>> namespaceMapping, bool skipDirectories)
        {
            try
            {
                string text = System.IO.File.ReadAllText(infile);
                CodeGen codegen = new CodeGen();
                codegen.AddSchema(text, namespaceMapping);

                codegen.GenerateCode();
                codegen.WriteTypes(outdir, skipDirectories);
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine("Exception occurred. " + ex.Message);
                return 1;
            }

            return 0;
        }

        static int GenSchema(List<string> infiles, string outdir, IEnumerable<KeyValuePair<string, string>> namespaceMapping)
        {
            try
            {
                var sn = new SchemaNames();
                CodeGen codegen = new CodeGen();
                var targetNs = new List<string>();

                if (infiles.Count == 1)
                {
                    FileAttributes attr = System.IO.File.GetAttributes(infiles.First());
                    if (attr.HasFlag(FileAttributes.Directory))
                    {
                        var dirInfo = new DirectoryInfo(infiles.First());
                        infiles = dirInfo.GetFiles("*.*", SearchOption.TopDirectoryOnly)
                                         .OrderBy(f => f.Name)
                                         .Select(f => f.FullName)
                                         .ToList();
                    }
                }

                var toRetry = new List<string>();
                foreach (var infile in infiles)
                {
                    FileAttributes attr = System.IO.File.GetAttributes(infile);
                    if (attr.HasFlag(FileAttributes.Directory))
                        continue;

                    Console.WriteLine($"Loading Schema from: [{infile}]");
                    string text = System.IO.File.ReadAllText(infile);

                    //try
                    {
                        Schema schema = Schema.Parse(text, sn);
                        var namespaces = GetNamespacesFromSchema(schema);

                        foreach (var n in namespaces)
                        {
                            if (!targetNs.Contains(n))
                            {
                                targetNs.Add(n);
                            }
                        }

                        codegen.AddSchema(schema);
                    }
                    //catch(Avro.SchemaParseException e)
                    //{
                    //    if (toRetry.Contains(infile))
                    //        toRetry.Remove(infile);
                    //    else
                    //        toRetry.Add(infile);
                    //}
                }

                foreach (var entry in namespaceMapping)
                    codegen.NamespaceMapping[entry.Key] = entry.Value;

                Console.WriteLine("Generating code ...");

                codegen.GenerateCode();
                codegen.WriteTypes(outdir, targetNs);

                return 0;
            }
            catch (Exception ex)
            {
                Console.WriteLine("Exception occurred. " + ex.Message);
                return 1;
            }
        }

        private static List<string> GetNamespacesFromSchema(Schema schema)
        {
            var result = new List<string>();

            if (schema is NamedSchema namedSchema && !result.Contains(namedSchema.Namespace))
            {
                result.Add(namedSchema.Namespace);
            }

            if (schema is UnionSchema unionSchema)
            {
                foreach (var s in unionSchema.Schemas)
                {
                    if (s is RecordSchema recordSchema && !result.Contains(recordSchema.Namespace))
                    {
                        result.Add(recordSchema.Namespace);
                    }
                }
            }

            return result;
        }
    }
}
