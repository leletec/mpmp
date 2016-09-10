# packgen.awk: generate Java code from packets file

# blank line separates records
BEGIN {RS = ""; FS = "\n"}

# skip comments
/^( \t)*#/ {next}

$1 ~ /(S->C)|(C->S)/ {
	for(ai = 3; ai <= NF; ai++) {
		gsub(/^( |\t)*/, "", $ai)
		argv[ai-2] = $ai     # 1-indexed
	}

	packet($1, $2, argv)
}

function packet(attr, name, args) {
	header(attr, name)
	attributes(attr, name)
	print ""
	execfn(attr, name, args)
	print ""
	errorfn(attr, name, args)

	if(attr ~ /game/) {
		print ""
		updater(attr, name, args)
	}

	footer()
}

# header: print header (everything before body of class)
function header(attr, name) {
	if(length(d) == 0)
		"date" | getline d

	printf("/* Generated at %s */\n\n", d)

	# XXX includes

	printf("/**\n * %s %s\n */\n", attr, name)
	print "public class", camel(name), "extends CmdFunc {"
}

# attributes: print attributes of class
function attributes(attr, name) {
	if(attr ~ /game/) {
		printf("\tprivate %sr %s;\n", camel(name), updatername(name))
	}
}

# execfn: print exec function that parses arguments; if the game attribute is given,
# call the updater function automatically
function execfn(attr, name, args,           i, a, typ, atype, aname, adoc, usage, parseints) {
	usage = name

	print "\t@Override\n\tpublic void exec(String line, Conn conn) {"
	print "\t\tString args[] = line.split(\" \");"

	# variable definitions
	for(i = 1; i <= length(args); i++) {
		split(args[i], a, "( |\t)+")
		atype[i] = a[1]
		aname[i] = a[2]
		adoc[i]  = a[3]

		typ = (atype[i] ~ /username/)?"String":atype[i]
		printf("\t\t%s %s;\n", typ, aname[i]);

		usage = usage " " adoc[i]
	}

	# argument count check
	printf("\n\t\tif(args.length < %d) {\n%s\t\t}\n\n", length(args)+1, Susage(usage))

	# fill variables with properly-typed values
	for(i = 1; i <= length(args); i++) {
		if(atype[i] ~ /username/) {
			printf("\t\t%s = String.join(" ", Arrays.copyOfRange(args, %d, args.length));\n", aname[i], i)
		} else if(atype[i] ~ /String/) {
			printf("\t\t%s = args[%d];\n", aname[i], i)
		} else if(atype[i] ~ /int/) {
			# hold off until later, consolidate all parseInts in a single try-catch
			parseints = parseints sprintf("\t\t\t%s = Integer.parseInt(args[%d]);\n", aname[i], i)
		}
	}

	printf("\n\t\ttry {\n%s\t\t} catch(NumberFormatException nfe) {\n%s\t\t}\n\n", parseints, Susage(usage))

	# generate updater-calling line
	if(attr ~ /game/) {
		printf("\t\t%s.%s(%s);\n", updatername(name), smallcamel(name), Sarg(atype, aname, 0))

	}

	print "\t}\n"
}

# Susage: return three-indented block that sends usage error and returns
function Susage(s) {
	return sprintf("\t\t\tconn.sendErr(ErrCode.Usage, \"%s\");\n\t\t\treturn;\n", s)
}

# Sarg: return comma-separated list of arguments with (withtypes != 0) or without types (withtypes == 0)
function Sarg(atype, aname, withtypes,        typ, i, ret) {
	for(i = 1; i <= length(atype); i++) {
		typ = (atype[i] ~ /username/)?"String":atype[i]

		if(i == length(atype))            # no comma after last arg
			ret = ret ((withtypes)?typ" ":"") aname[i]
		else
			ret = ret ((withtypes)?typ" ":"") aname[i] ", "
	}

	return ret
}

# errorfn: print error handler
# XXX not fillable atm
function errorfn(attr, name, args) {
	print "\t@Override\n\tpublic void error(ErrCode err, String line, Conn conn) {/* herp derp */}"
}

# updater: print updater interface and add method
function updater(attr, name, args,       a, atype, aname, cm, scm, un) {
	# XXX duplicate work!
	for(i = 1; i <= length(args); i++) {
		split(args[i], a, "( |\t)+")
		atype[i] = a[1]
		aname[i] = a[2]
	}

	cm = camel(name)
	scm = smallcamel(name)
	un = updatername(name)
	printf("\tpublic interface %sr {\n\t\tpublic void %s(%s);\n\t}\n", cm, scm, Sarg(atype, aname, 1))
	print ""
	printf("\tpublic void add%sr(%sr %s) {\n\t\tthis.%s = %s;\n\t}\n", cm, cm, un, un, un)
}

# footer: print footer
function footer() {
	print "}"
}

# camel: return CamelCase form of packet name
function camel(s,       words, chars, i, ret) {
	split(s, words, "-")                    # "chat-update" -> "chat", "update"

	for(i = 1; i <= length(words); i++) {
		split(words[i], chars, "")      # "chat" -> "c", "h", "a", "t"
		ret = ret toupper(chars[1])     # "c" -> "C", add to return value
		ret = ret substr(words[i], 2, length(words[i]))
	}

	return ret
}

# smallcamel: return camelCase instead of CamelCase
function smallcamel(s,      words, chars, i, ret) {
	split(s, words, "-")

	for(i = 1; i <= length(words); i++) {
		split(words[i], chars, "")

		if(i == 1) {                    # skip first
			ret = ret words[i]
			continue
		}

		ret = ret toupper(chars[1])
		ret = ret substr(words[i], 2, length(words[i]))
	}

	return ret
}

# updatername: return two-letter name of Updater instance; "money-update" -> "mu"
function updatername(name,        words, chars, i, ret) {
	split(name, words, "-")

	for(i = 1; i <= length(words); i++) {
		split(words[i], chars, "")      # "chat" -> "c", "h", "a", "t"
		ret = ret chars[1]
	}

	return ret
}
